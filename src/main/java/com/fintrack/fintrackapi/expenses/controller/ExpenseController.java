package com.fintrack.fintrackapi.expenses.controller;

import com.fintrack.fintrackapi.expenses.model.CreateSharedExpenseRequest;
import com.fintrack.fintrackapi.expenses.model.SharedExpenseResponse;
import com.fintrack.fintrackapi.expenses.service.BalanceService;
import com.fintrack.fintrackapi.expenses.service.SharedExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import com.fintrack.fintrackapi.expenses.model.BalanceResponse;

@RestController
@RequestMapping("/api")
public class ExpenseController {

    private final SharedExpenseService sharedExpenseService;
    private final BalanceService balanceService;

    public ExpenseController(SharedExpenseService sharedExpenseService, BalanceService balanceService) {
        this.sharedExpenseService = sharedExpenseService;
        this.balanceService = balanceService;
    }

    /** Creates a shared expense for the authenticated user. */
    @PostMapping("/shared-expenses")
    @ResponseStatus(HttpStatus.CREATED)
    public SharedExpenseResponse create(Authentication authentication,
                                        @Valid @RequestBody CreateSharedExpenseRequest request) {
        return sharedExpenseService.create(authentication.getName(), request);
    }

    /** Lists net pending balances visible to the authenticated user only. */
    @GetMapping("/balances")
    public List<BalanceResponse> getBalances(Authentication authentication) {
        return balanceService.getPendingBalances(authentication.getName());
    }
}
