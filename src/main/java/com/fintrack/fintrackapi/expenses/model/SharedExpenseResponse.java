package com.fintrack.fintrackapi.expenses.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SharedExpenseResponse(
        UUID id,
        String creatorId,
        String description,
        BigDecimal totalAmount,
        SplitType splitType,
        List<ParticipantShare> participants,
        Instant createdAt) {

    public static SharedExpenseResponse from(SharedExpenseEntity expense) {
        List<ParticipantShare> shares = expense.getParticipants().stream()
                .map(participant -> new ParticipantShare(participant.getUserId(), participant.getShareAmount()))
                .toList();
        return new SharedExpenseResponse(expense.getId(), expense.getCreatorId(), expense.getDescription(),
                expense.getTotalAmount(), expense.getSplitType(), shares, expense.getCreatedAt());
    }

    public record ParticipantShare(String userId, BigDecimal shareAmount) {
    }
}
