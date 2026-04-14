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
    void testGenerateSchedule_StandardAnnuity() {
        // Arrange: 10,000 for 12 months at 2% + 3% = 5%
        LoanApplication app = LoanApplication.builder()
                .amount(new BigDecimal("10000.00"))
                .termMonths(12)
                .margin(new BigDecimal("2.0"))
                .euribor(new BigDecimal("3.0")) 
                .build();
        
        LocalDate startDate = LocalDate.of(2023, 1, 1);

        // Act
        List<PaymentInstallment> schedule = generator.generateSchedule(app, startDate);

        // Assert
        assertThat(schedule).hasSize(12);
        
        // Sum of all principal payments should equal exact loan amount
        BigDecimal totalPrincipal = schedule.stream()
                .map(PaymentInstallment::getPrincipalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(totalPrincipal).isEqualByComparingTo(new BigDecimal("10000.00"));
        
        // Final balance should be perfectly 0
        assertThat(schedule.get(11).getRemainingBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        
        // Verify payment dates
        assertThat(schedule.get(0).getPaymentDate()).isEqualTo(LocalDate.of(2023, 1, 1));
        assertThat(schedule.get(1).getPaymentDate()).isEqualTo(LocalDate.of(2023, 2, 1));
        assertThat(schedule.get(11).getPaymentDate()).isEqualTo(LocalDate.of(2023, 12, 1));
    }
    
    @Test
    void testGenerateSchedule_ZeroInterest() {
        // Arrange
        LoanApplication app = LoanApplication.builder()
                .amount(new BigDecimal("12000.00"))
                .termMonths(12)
                .margin(BigDecimal.ZERO)
                .euribor(BigDecimal.ZERO)
                .build();
        
        // Act
        List<PaymentInstallment> schedule = generator.generateSchedule(app, LocalDate.now());
        
        // Assert
        assertThat(schedule).hasSize(12);
        assertThat(schedule.get(0).getPaymentAmount()).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(schedule.get(0).getInterestAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(schedule.get(11).getRemainingBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
