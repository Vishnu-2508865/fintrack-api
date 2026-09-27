package com.fintrack.fintrackapi.transactions.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false, length = 128)
    private String ownerId;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected TransactionEntity() {
    }

    /** Creates a transaction with an immutable owner and creation timestamp. */
    public TransactionEntity(String ownerId, String description, BigDecimal amount) {
        this.ownerId = ownerId;
        this.description = description;
        this.amount = amount;
        this.createdAt = Instant.now();
    }

    /** Returns the database identifier. */
    public UUID getId() {
        return id;
    }

    /** Returns the authenticated owner identifier. */
    public String getOwnerId() {
        return ownerId;
    }

    /** Returns the transaction description. */
    public String getDescription() {
        return description;
    }

    /** Returns the exact decimal amount. */
    public BigDecimal getAmount() {
        return amount;
    }

    /** Returns the transaction creation timestamp in UTC. */
    public Instant getCreatedAt() {
        return createdAt;
    }
}
