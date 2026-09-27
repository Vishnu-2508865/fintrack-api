package com.fintrack.fintrackapi.transactions.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(UUID id, String description, BigDecimal amount, Instant createdAt) {

    /** Maps a persisted transaction to its public API representation. */
    public static TransactionResponse from(TransactionEntity transaction) {
        return new TransactionResponse(transaction.getId(), transaction.getDescription(),
                transaction.getAmount(), transaction.getCreatedAt());
    }
}
