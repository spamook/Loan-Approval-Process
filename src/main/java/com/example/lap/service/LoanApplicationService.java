package com.example.lap.service;

import com.example.lap.dto.LoanApplicationRequest;
import com.example.lap.dto.LoanApplicationResponse;
import com.example.lap.entity.ApplicationStatus;
import com.example.lap.entity.LoanApplication;
import com.example.lap.entity.PaymentInstallment;
import com.example.lap.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanApplicationService {

    private final LoanApplicationRepository repository;
    private final IsikukoodParser isikukoodParser;
    private final PaymentScheduleGenerator scheduleGenerator;
    private final LoanApplicationMapper mapper;

    @Value("${loan.age-limit:70}")
    private int ageLimit;

    @Transactional
    public LoanApplicationResponse applyForLoan(LoanApplicationRequest request) {
        boolean hasActive = repository.existsByPersonalCodeAndStatusIn(
                request.getPersonalCode(), List.of(ApplicationStatus.IN_REVIEW)
        );
        if (hasActive) {
            throw new IllegalArgumentException("Client already has an active application in review");
        }

        LoanApplication application = LoanApplication.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .personalCode(request.getPersonalCode())
                .termMonths(request.getTermMonths())
                .margin(request.getMargin())
                .euribor(request.getEuribor())
                .amount(request.getAmount())
                .build();

        int clientAge = isikukoodParser.getAge(request.getPersonalCode());

        if (clientAge > ageLimit) {
            application.setStatus(ApplicationStatus.REJECTED);
            application.setRejectionReason("CUSTOMER_TOO_OLD");
            return mapper.toResponse(repository.save(application));
        }

        application.setStatus(ApplicationStatus.IN_REVIEW);
        List<PaymentInstallment> schedule = scheduleGenerator.generateSchedule(application, LocalDate.now());
        application.getPaymentSchedule().addAll(schedule);

        return mapper.toResponse(repository.save(application));
    }
    
    @Transactional(readOnly = true)
    public LoanApplicationResponse getById(Long id) {
        LoanApplication app = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        return mapper.toResponse(app);
    }

    @Transactional
    public LoanApplicationResponse approve(Long id) {
        LoanApplication app = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        if (!ApplicationStatus.IN_REVIEW.equals(app.getStatus())) {
            throw new IllegalStateException("Only IN_REVIEW applications can be approved");
        }

        app.setStatus(ApplicationStatus.APPROVED);
        return mapper.toResponse(repository.save(app));
    }

    @Transactional
    public LoanApplicationResponse reject(Long id, String reason) {
        LoanApplication app = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        if (!ApplicationStatus.IN_REVIEW.equals(app.getStatus())) {
            throw new IllegalStateException("Only IN_REVIEW applications can be rejected");
        }

        app.setStatus(ApplicationStatus.REJECTED);
        app.setRejectionReason(reason);
        return mapper.toResponse(repository.save(app));
    }

    @Transactional
    public LoanApplicationResponse regenerateSchedule(Long id, LoanApplicationRequest request) {
        LoanApplication app = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        if (!ApplicationStatus.IN_REVIEW.equals(app.getStatus())) {
            throw new IllegalStateException("Only IN_REVIEW applications can be modified");
        }

        app.setTermMonths(request.getTermMonths());
        app.setMargin(request.getMargin());
        app.setEuribor(request.getEuribor());
        app.setAmount(request.getAmount());

        app.getPaymentSchedule().clear();
        List<PaymentInstallment> newSchedule = scheduleGenerator.generateSchedule(app, LocalDate.now());
        app.getPaymentSchedule().addAll(newSchedule);

        return mapper.toResponse(repository.save(app));
    }
}
