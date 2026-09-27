package com.fintrack.fintrackapi.expenses.service;

import com.fintrack.fintrackapi.expenses.model.BalanceResponse;
import com.fintrack.fintrackapi.expenses.model.ExpenseParticipantEntity;
import com.fintrack.fintrackapi.expenses.model.SharedExpenseEntity;
import com.fintrack.fintrackapi.expenses.repository.SharedExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class BalanceService {

    private final SharedExpenseRepository expenseRepository;

    public BalanceService(SharedExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    /** Calculates each counterparty's net pending amount; positive means the user owes them. */
    @Transactional(readOnly = true)
    public List<BalanceResponse> getPendingBalances(String userId) {
        Map<String, PairAmounts> amountsByCounterparty = new TreeMap<>();
        for (SharedExpenseEntity expense : expenseRepository.findAllInvolvingUser(userId)) {
            addExpenseDebts(userId, expense, amountsByCounterparty);
        }
        List<BalanceResponse> balances = new ArrayList<>();
        amountsByCounterparty.forEach((otherUserId, amounts) -> {
            BigDecimal net = amounts.youOwe.subtract(amounts.theyOwe);
            if (net.signum() > 0) {
                balances.add(new BalanceResponse(otherUserId, net, BigDecimal.ZERO, net));
            } else if (net.signum() < 0) {
                BigDecimal owedToUser = net.negate();
                balances.add(new BalanceResponse(otherUserId, BigDecimal.ZERO, owedToUser, net));
            }
        });
        balances.sort(Comparator.comparing(BalanceResponse::otherUserId));
        return List.copyOf(balances);
    }

    private static void addExpenseDebts(
            String userId, SharedExpenseEntity expense, Map<String, PairAmounts> amountsByCounterparty) {
        for (ExpenseParticipantEntity participant : expense.getParticipants()) {
            if (participant.getUserId().equals(expense.getCreatorId())) {
                continue;
            }
            if (participant.getUserId().equals(userId)) {
                PairAmounts amounts = amountsByCounterparty.computeIfAbsent(
                        expense.getCreatorId(), ignored -> new PairAmounts());
                amounts.youOwe = amounts.youOwe.add(participant.getShareAmount());
            } else if (expense.getCreatorId().equals(userId)) {
                PairAmounts amounts = amountsByCounterparty.computeIfAbsent(
                        participant.getUserId(), ignored -> new PairAmounts());
                amounts.theyOwe = amounts.theyOwe.add(participant.getShareAmount());
            }
        }
    }

    private static final class PairAmounts {
        private BigDecimal youOwe = BigDecimal.ZERO;
        private BigDecimal theyOwe = BigDecimal.ZERO;
    }
}
