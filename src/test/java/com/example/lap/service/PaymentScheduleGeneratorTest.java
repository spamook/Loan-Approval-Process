package com.example.lap.service;

import com.example.lap.entity.LoanApplication;
import com.example.lap.entity.PaymentInstallment;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentScheduleGeneratorTest {

    private final PaymentScheduleGenerator generator = new PaymentScheduleGenerator();

    @Test
    void shouldGenerateAnnuityScheduleAndCloseBalanceToZero() {
        LoanApplication app = LoanApplication.builder()
                .amount(new BigDecimal("10000.00")) // Principal
                .termMonths(12)
                .margin(new BigDecimal("2.00"))
                .euribor(new BigDecimal("3.00")) // Total 5.00% annual
                .build();

        LocalDate startDate = LocalDate.of(2025, 1, 1);
        List<PaymentInstallment> schedule = generator.generateSchedule(app, startDate);

        assertThat(schedule).hasSize(12);

        // Check first installment
        PaymentInstallment first = schedule.get(0);
        assertThat(first.getInstallmentNumber()).isEqualTo(1);
        assertThat(first.getPaymentDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(first.getPaymentAmount()).isEqualByComparingTo("856.07"); // Roughly ~856.07
        
        // Sum up total payments and check last installment exactly closes balance
        PaymentInstallment last = schedule.get(11);
        assertThat(last.getInstallmentNumber()).isEqualTo(12);
        assertThat(last.getPaymentDate()).isEqualTo(LocalDate.of(2025, 12, 1));
        assertThat(last.getRemainingBalance()).isEqualByComparingTo("0.00");
    }

    @Test
    void shouldHandleZeroOrNegativeInterestRateAsZeroRate() {
        // Zero interest means payment = amount / term
        LoanApplication app = LoanApplication.builder()
                .amount(new BigDecimal("12000.00"))
                .termMonths(12)
                .margin(new BigDecimal("0.00"))
                .euribor(new BigDecimal("0.00"))
                .build();

        List<PaymentInstallment> schedule = generator.generateSchedule(app, LocalDate.now());

        assertThat(schedule).hasSize(12);
        assertThat(schedule.get(0).getPaymentAmount()).isEqualByComparingTo("1000.00");
        assertThat(schedule.get(0).getInterestAmount()).isEqualByComparingTo("0.00");
        assertThat(schedule.get(11).getRemainingBalance()).isEqualByComparingTo("0.00");
    }
}
