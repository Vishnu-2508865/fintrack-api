package com.fintrack.fintrackapi.expenses.model;

import java.math.BigDecimal;

public record BalanceResponse(
        String otherUserId,
        BigDecimal youOwe,
        BigDecimal theyOwe,
        BigDecimal netAmount) {
}
