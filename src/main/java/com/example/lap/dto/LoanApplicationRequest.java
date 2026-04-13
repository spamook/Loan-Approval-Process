package com.example.lap.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 32, message = "First name cannot exceed 32 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 32, message = "Last name cannot exceed 32 characters")
    private String lastName;

    @NotBlank(message = "Personal code is required")
    @Pattern(regexp = "^\\d{11}$", message = "Personal code must be exactly 11 digits")
    private String personalCode;

    @NotNull(message = "Term is required")
    @Min(value = 6, message = "Minimum term is 6 months")
    @Max(value = 360, message = "Maximum term is 360 months")
    private Integer termMonths;

    @NotNull(message = "Margin is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Margin must be greater than or equal to 0")
    private BigDecimal margin;

    @NotNull(message = "Euribor is required")
    private BigDecimal euribor;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "5000.0", inclusive = true, message = "Amount must be at least 5000")
    private BigDecimal amount;
}
