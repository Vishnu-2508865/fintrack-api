package com.fintrack.fintrackapi.expenses.service;

import com.fintrack.fintrackapi.common.InvalidRequestException;
import com.fintrack.fintrackapi.expenses.model.CreateSharedExpenseRequest;
import com.fintrack.fintrackapi.expenses.model.ExpenseParticipantEntity;
import com.fintrack.fintrackapi.expenses.model.ParticipantShareRequest;
import com.fintrack.fintrackapi.expenses.model.SharedExpenseEntity;
import com.fintrack.fintrackapi.expenses.model.SharedExpenseResponse;
import com.fintrack.fintrackapi.expenses.model.SplitType;
import com.fintrack.fintrackapi.expenses.repository.SharedExpenseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SharedExpenseService {

    private static final Logger log = LoggerFactory.getLogger(SharedExpenseService.class);
    private static final BigDecimal CENT = new BigDecimal("0.01");

    private final SharedExpenseRepository expenseRepository;

    public SharedExpenseService(SharedExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    /** Creates a shared expense and persists validated participant shares. */
    @Transactional
    public SharedExpenseResponse create(String creatorId, CreateSharedExpenseRequest request) {
        BigDecimal total = normalizeAmount(request.totalAmount(), "totalAmount");
        List<ParticipantShareRequest> requestedParticipants = request.participants();
        validateParticipants(creatorId, requestedParticipants);

        List<BigDecimal> shares = request.splitType() == SplitType.EQUAL
                ? equalShares(total, requestedParticipants.size())
                : customShares(total, requestedParticipants);

        List<ExpenseParticipantEntity> participants = new ArrayList<>(requestedParticipants.size());
        for (int index = 0; index < requestedParticipants.size(); index++) {
            participants.add(new ExpenseParticipantEntity(
                    requestedParticipants.get(index).userId().trim(), shares.get(index)));
        }
        SharedExpenseEntity expense = new SharedExpenseEntity(creatorId, request.description().trim(),
                total, request.splitType(), participants);
        SharedExpenseResponse response = SharedExpenseResponse.from(expenseRepository.save(expense));
        log.info("Created shared expense {} for creator {}", response.id(), creatorId);
        return response;
    }

    private static void validateParticipants(String creatorId, List<ParticipantShareRequest> participants) {
        if (participants.size() < 2) {
            throw new InvalidRequestException("An expense must include at least two participants");
        }
        Set<String> userIds = new HashSet<>();
        boolean creatorIncluded = false;
        for (ParticipantShareRequest participant : participants) {
            String userId = participant.userId().trim();
            if (!userIds.add(userId)) {
                throw new InvalidRequestException("Participant user IDs must be unique");
            }
            creatorIncluded |= creatorId.equals(userId);
        }
        if (!creatorIncluded) {
            throw new InvalidRequestException("The creator must be included as a participant");
        }
    }

    private static List<BigDecimal> equalShares(BigDecimal total, int count) {
        if (total.compareTo(CENT.multiply(BigDecimal.valueOf(count))) < 0) {
            throw new InvalidRequestException("Total amount is too small to give each participant a positive share");
        }
        BigDecimal baseShare = total.divide(BigDecimal.valueOf(count), 2, RoundingMode.DOWN);
        BigDecimal remainder = total.subtract(baseShare.multiply(BigDecimal.valueOf(count)));
        List<BigDecimal> shares = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            BigDecimal share = baseShare;
            if (remainder.compareTo(CENT) >= 0) {
                share = share.add(CENT);
                remainder = remainder.subtract(CENT);
            }
            shares.add(share);
        }
        return shares;
    }

    private static List<BigDecimal> customShares(
            BigDecimal total, List<ParticipantShareRequest> participants) {
        List<BigDecimal> shares = new ArrayList<>(participants.size());
        BigDecimal sum = BigDecimal.ZERO;
        for (ParticipantShareRequest participant : participants) {
            if (participant.shareAmount() == null) {
                throw new InvalidRequestException("Custom splits require an amount for every participant");
            }
            BigDecimal share = normalizeAmount(participant.shareAmount(), "shareAmount");
            if (share.signum() <= 0) {
                throw new InvalidRequestException("Participant shares must be greater than zero");
            }
            shares.add(share);
            sum = sum.add(share);
        }
        if (sum.compareTo(total) != 0) {
            throw new InvalidRequestException("Custom participant shares must equal the total amount");
        }
        return shares;
    }

    private static BigDecimal normalizeAmount(BigDecimal amount, String fieldName) {
        try {
            if (amount.signum() <= 0) {
                throw new InvalidRequestException(fieldName + " must be greater than zero");
            }
            return amount.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException exception) {
            throw new InvalidRequestException(fieldName + " must have no more than two decimal places");
        }
    }
}
