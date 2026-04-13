package com.example.lap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInstallmentDto {

    private Integer installmentNumber;
    private LocalDate paymentDate;
    private BigDecimal paymentAmount;
    private BigDecimal interestAmount;
    private BigDecimal principalAmount;
    private BigDecimal remainingBalance;
}
