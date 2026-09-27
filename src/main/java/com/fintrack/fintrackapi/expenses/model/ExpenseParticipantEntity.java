package com.fintrack.fintrackapi.expenses.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "expense_participants")
public class ExpenseParticipantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "expense_id", nullable = false)
    private SharedExpenseEntity expense;

    @Column(nullable = false, length = 128)
    private String userId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal shareAmount;

    @Column(nullable = false)
    private int participantOrder;

    protected ExpenseParticipantEntity() {
    }

    public ExpenseParticipantEntity(String userId, BigDecimal shareAmount) {
        this.userId = userId;
        this.shareAmount = shareAmount;
    }

    void setExpense(SharedExpenseEntity expense) {
        this.expense = expense;
    }

    void setParticipantOrder(int participantOrder) {
        this.participantOrder = participantOrder;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getShareAmount() {
        return shareAmount;
    }
}
