package com.example.lap.dto;

import com.example.lap.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String personalCode;
    private Integer termMonths;
    private BigDecimal margin;
    private BigDecimal euribor;
    private BigDecimal amount;
    private ApplicationStatus status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    
    private List<PaymentInstallmentDto> paymentSchedule;
}
