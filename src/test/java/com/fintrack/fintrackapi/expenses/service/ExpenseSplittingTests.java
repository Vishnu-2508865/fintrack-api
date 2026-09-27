package com.fintrack.fintrackapi.expenses.service;

import com.fintrack.fintrackapi.common.InvalidRequestException;
import com.fintrack.fintrackapi.common.ResourceNotFoundException;
import com.fintrack.fintrackapi.expenses.model.BalanceResponse;
import com.fintrack.fintrackapi.expenses.model.CreateSharedExpenseRequest;
import com.fintrack.fintrackapi.expenses.model.ExpenseParticipantEntity;
import com.fintrack.fintrackapi.expenses.model.ParticipantShareRequest;
import com.fintrack.fintrackapi.expenses.model.SharedExpenseEntity;
import com.fintrack.fintrackapi.expenses.model.SharedExpenseResponse;
import com.fintrack.fintrackapi.expenses.model.SplitType;
import com.fintrack.fintrackapi.expenses.repository.SharedExpenseRepository;
import com.fintrack.fintrackapi.transactions.repository.TransactionRepository;
import com.fintrack.fintrackapi.transactions.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseSplittingTests {

    @Mock
    private SharedExpenseRepository sharedExpenseRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private SharedExpenseService sharedExpenseService;

    @Test
    void equalSplitAmongThreeParticipantsDistributesRemainderExactly() {
        when(sharedExpenseRepository.save(any(SharedExpenseEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        CreateSharedExpenseRequest request = request("10.00", SplitType.EQUAL,
                participant("alice", null), participant("bob", null), participant("carol", null));

        SharedExpenseResponse response = sharedExpenseService.create("alice", request);

        assertEquals(List.of(new BigDecimal("3.34"), new BigDecimal("3.33"), new BigDecimal("3.33")),
                response.participants().stream().map(SharedExpenseResponse.ParticipantShare::shareAmount).toList());
        assertEquals(new BigDecimal("10.00"), response.participants().stream()
                .map(SharedExpenseResponse.ParticipantShare::shareAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    @Test
    void customSplitAcceptsAmountsThatMatchTotal() {
        when(sharedExpenseRepository.save(any(SharedExpenseEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SharedExpenseResponse response = sharedExpenseService.create("alice",
                request("30.00", SplitType.CUSTOM,
                        participant("alice", "10.00"), participant("bob", "20.00")));

        assertEquals(new BigDecimal("30.00"), response.participants().stream()
                .map(SharedExpenseResponse.ParticipantShare::shareAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    @Test
    void customSplitRejectsAmountsThatDoNotMatchTotal() {
        CreateSharedExpenseRequest request = request("30.00", SplitType.CUSTOM,
                participant("alice", "10.00"), participant("bob", "19.99"));

        assertThrows(InvalidRequestException.class, () -> sharedExpenseService.create("alice", request));
        verify(sharedExpenseRepository, never()).save(any(SharedExpenseEntity.class));
    }

    @Test
    void balancesNetMultipleExpensesBetweenTwoUsers() {
        when(sharedExpenseRepository.findAllInvolvingUser("alice")).thenReturn(List.of(
                expense("alice", share("alice", "30.00"), share("bob", "30.00")),
                expense("bob", share("bob", "10.00"), share("alice", "10.00")),
                expense("alice", share("alice", "5.00"), share("bob", "10.00")),
                expense("bob", share("bob", "8.00"), share("alice", "10.00"))));
        BalanceService balanceService = new BalanceService(sharedExpenseRepository);

        List<BalanceResponse> balances = balanceService.getPendingBalances("alice");

        assertEquals(1, balances.size());
        assertEquals("bob", balances.get(0).otherUserId());
        assertEquals(BigDecimal.ZERO, balances.get(0).youOwe());
        assertEquals(new BigDecimal("20.00"), balances.get(0).theyOwe());
        assertEquals(new BigDecimal("-20.00"), balances.get(0).netAmount());
    }

    @Test
    void expenseWithOneParticipantIsRejected() {
        CreateSharedExpenseRequest request = request("10.00", SplitType.EQUAL,
                participant("alice", null));

        assertThrows(InvalidRequestException.class, () -> sharedExpenseService.create("alice", request));
        verify(sharedExpenseRepository, never()).save(any(SharedExpenseEntity.class));
    }

    @Test
    void userCannotDeleteAnotherUsersTransaction() {
        UUID transactionId = UUID.randomUUID();
        when(transactionRepository.findByIdAndOwnerId(transactionId, "mallory")).thenReturn(Optional.empty());
        TransactionService transactionService = new TransactionService(transactionRepository);

        assertThrows(ResourceNotFoundException.class, () -> transactionService.delete("mallory", transactionId));
        verify(transactionRepository, never()).delete(any());
    }

    private static CreateSharedExpenseRequest request(
            String total, SplitType splitType, ParticipantShareRequest... participants) {
        return new CreateSharedExpenseRequest("Shared meal", new BigDecimal(total), splitType,
                List.of(participants));
    }

    private static ParticipantShareRequest participant(String userId, String amount) {
        return new ParticipantShareRequest(userId, amount == null ? null : new BigDecimal(amount));
    }

    private static ExpenseParticipantEntity share(String userId, String amount) {
        return new ExpenseParticipantEntity(userId, new BigDecimal(amount));
    }

    private static SharedExpenseEntity expense(String creatorId, ExpenseParticipantEntity... shares) {
        return new SharedExpenseEntity(creatorId, "Shared item", new BigDecimal("100.00"),
                SplitType.CUSTOM, List.of(shares));
    }
}
