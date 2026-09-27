package com.fintrack.fintrackapi.expenses.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "shared_expenses")
public class SharedExpenseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false, length = 128)
    private String creatorId;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private SplitType splitType;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("participantOrder ASC")
    private List<ExpenseParticipantEntity> participants = new ArrayList<>();

    protected SharedExpenseEntity() {
    }

    public SharedExpenseEntity(String creatorId, String description, BigDecimal totalAmount,
                               SplitType splitType, List<ExpenseParticipantEntity> participants) {
        this.creatorId = creatorId;
        this.description = description;
        this.totalAmount = totalAmount;
        this.splitType = splitType;
        this.createdAt = Instant.now();
        for (int index = 0; index < participants.size(); index++) {
            ExpenseParticipantEntity participant = participants.get(index);
            participant.setExpense(this);
            participant.setParticipantOrder(index);
            this.participants.add(participant);
        }
    }

    public UUID getId() {
        return id;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public SplitType getSplitType() {
        return splitType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<ExpenseParticipantEntity> getParticipants() {
        return List.copyOf(participants);
    }
}
