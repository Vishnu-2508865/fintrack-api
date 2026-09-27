package com.fintrack.fintrackapi.transactions.controller;

import com.fintrack.fintrackapi.transactions.model.CreateTransactionRequest;
import com.fintrack.fintrackapi.transactions.model.TransactionResponse;
import com.fintrack.fintrackapi.transactions.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /** Creates a transaction for the authenticated user. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(Authentication authentication,
                                      @Valid @RequestBody CreateTransactionRequest request) {
        return transactionService.create(authentication.getName(), request);
    }

    /** Lists transactions owned by the authenticated user. */
    @GetMapping
    public List<TransactionResponse> getByUser(Authentication authentication) {
        return transactionService.getByUser(authentication.getName());
    }

    /** Deletes one transaction owned by the authenticated user. */
    @DeleteMapping("/{transactionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication authentication, @PathVariable UUID transactionId) {
        transactionService.delete(authentication.getName(), transactionId);
    }

    /** Deletes all transactions owned by the authenticated user. */
    @DeleteMapping
    public Map<String, Long> deleteAll(Authentication authentication) {
        return Map.of("deleted", transactionService.deleteAll(authentication.getName()));
    }
}
