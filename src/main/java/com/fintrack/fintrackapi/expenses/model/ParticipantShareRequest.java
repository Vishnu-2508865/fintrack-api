package com.fintrack.fintrackapi.expenses.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ParticipantShareRequest(
        @NotBlank @Size(max = 128) String userId,
        @Digits(integer = 17, fraction = 2) @DecimalMin(value = "0.01") BigDecimal shareAmount) {
}
