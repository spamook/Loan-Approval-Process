package com.example.lap.service;

import com.example.lap.entity.LoanApplication;
import com.example.lap.entity.PaymentInstallment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentScheduleGenerator {

    public List<PaymentInstallment> generateSchedule(LoanApplication application, LocalDate startDate) {
        List<PaymentInstallment> schedule = new ArrayList<>();
        
        BigDecimal principal = application.getAmount();
        int periods = application.getTermMonths();
        
        // Annual percentage rate = margin + euribor. Needs to be divided by 100.
        BigDecimal annualRate = application.getMargin().add(application.getEuribor()).divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        
        BigDecimal monthlyPayment;
        if (monthlyRate.compareTo(BigDecimal.ZERO) <= 0) {
            // Negative or zero interest rates behave as 0 interest loans
            monthlyRate = BigDecimal.ZERO;
            monthlyPayment = principal.divide(BigDecimal.valueOf(periods), 2, RoundingMode.HALF_UP);
        } else {
            // Annuity Formula: A = P * (r(1+r)^n) / ((1+r)^n - 1)
            BigDecimal onePlusRToN = BigDecimal.ONE.add(monthlyRate).pow(periods, MathContext.DECIMAL64);
            BigDecimal numerator = monthlyRate.multiply(onePlusRToN);
            BigDecimal denominator = onePlusRToN.subtract(BigDecimal.ONE);
            monthlyPayment = principal.multiply(numerator).divide(denominator, 2, RoundingMode.HALF_UP);
        }

        BigDecimal remainingBalance = principal;
        LocalDate currentDate = startDate;

        for (int i = 1; i <= periods; i++) {
            BigDecimal interestPayment = remainingBalance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalPayment = monthlyPayment.subtract(interestPayment);
            
            if (i == periods) {
                // Adjust the very last payment to exactly zero out the remaining balance
                principalPayment = remainingBalance;
                monthlyPayment = principalPayment.add(interestPayment);
                remainingBalance = BigDecimal.ZERO;
            } else {
                remainingBalance = remainingBalance.subtract(principalPayment);
            }
            
            PaymentInstallment installment = PaymentInstallment.builder()
                    .loanApplication(application)
                    .installmentNumber(i)
                    .paymentDate(currentDate)
                    .paymentAmount(monthlyPayment)
                    .interestAmount(interestPayment)
                    .principalAmount(principalPayment)
                    .remainingBalance(remainingBalance)
                    .build();
                    
            schedule.add(installment);
            currentDate = currentDate.plusMonths(1);
        }
        
        return schedule;
    }
}
