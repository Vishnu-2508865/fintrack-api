package com.fintrack.fintrackapi.expenses.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record CreateSharedExpenseRequest(
        @NotBlank @Size(max = 200) String description,
        @NotNull @DecimalMin(value = "0.01") @Digits(integer = 17, fraction = 2) BigDecimal totalAmount,
        @NotNull SplitType splitType,
        @NotEmpty List<@Valid ParticipantShareRequest> participants) {
}
