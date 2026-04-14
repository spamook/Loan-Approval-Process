package com.example.lap.service;

import com.example.lap.dto.LoanApplicationResponse;
import com.example.lap.dto.PaymentInstallmentDto;
import com.example.lap.entity.LoanApplication;
import com.example.lap.entity.PaymentInstallment;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LoanApplicationMapper {

    public LoanApplicationResponse toResponse(LoanApplication entity) {
        List<PaymentInstallmentDto> schedule = Collections.emptyList();
        if (entity.getPaymentSchedule() != null) {
            schedule = entity.getPaymentSchedule().stream()
                    .map(this::toInstallmentDto)
                    .collect(Collectors.toList());
        }

        return LoanApplicationResponse.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .personalCode(entity.getPersonalCode())
                .termMonths(entity.getTermMonths())
                .margin(entity.getMargin())
                .euribor(entity.getEuribor())
                .amount(entity.getAmount())
                .status(entity.getStatus())
                .rejectionReason(entity.getRejectionReason())
                .createdAt(entity.getCreatedAt())
                .paymentSchedule(schedule)
                .build();
    }

    private PaymentInstallmentDto toInstallmentDto(PaymentInstallment inst) {
        return PaymentInstallmentDto.builder()
                .installmentNumber(inst.getInstallmentNumber())
                .paymentDate(inst.getPaymentDate())
                .paymentAmount(inst.getPaymentAmount())
                .interestAmount(inst.getInterestAmount())
                .principalAmount(inst.getPrincipalAmount())
                .remainingBalance(inst.getRemainingBalance())
                .build();
    }
}
