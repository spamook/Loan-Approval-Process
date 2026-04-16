package com.example.lap.service;

import com.example.lap.dto.LoanApplicationRequest;
import com.example.lap.dto.LoanApplicationResponse;
import com.example.lap.entity.ApplicationStatus;
import com.example.lap.entity.LoanApplication;
import com.example.lap.repository.LoanApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationServiceTest {

    @Mock
    private LoanApplicationRepository repository;

    @Mock
    private IsikukoodParser isikukoodParser;

    @Mock
    private PaymentScheduleGenerator scheduleGenerator;

    @Mock
    private LoanApplicationMapper mapper;

    @InjectMocks
    private LoanApplicationService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "ageLimit", 70);
    }

    @Test
    void shouldRejectWhenUserIsTooOld() {
        LoanApplicationRequest request = new LoanApplicationRequest();
        request.setPersonalCode("34001010000"); // Very old

        when(repository.existsByPersonalCodeAndStatusIn(any(), any())).thenReturn(false);
        when(isikukoodParser.getAge("34001010000")).thenReturn(85);

        // Capture saved entity
        when(repository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        LoanApplicationResponse mockedResponse = new LoanApplicationResponse();
        when(mapper.toResponse(any())).thenReturn(mockedResponse);

        LoanApplicationResponse response = service.applyForLoan(request);

        verify(repository).save(argThat(entity -> 
            entity.getStatus() == ApplicationStatus.REJECTED &&
            "CUSTOMER_TOO_OLD".equals(entity.getRejectionReason())
        ));
    }

    @Test
    void shouldThrowWhenActiveApplicationExists() {
        LoanApplicationRequest request = new LoanApplicationRequest();
        request.setPersonalCode("50001010000");

        when(repository.existsByPersonalCodeAndStatusIn(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> service.applyForLoan(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Client already has an active application in review");
    }

    @Test
    void shouldSuccessfullyApplyForLoan() {
        LoanApplicationRequest request = new LoanApplicationRequest();
        request.setPersonalCode("50001010000");
        request.setTermMonths(12);

        when(repository.existsByPersonalCodeAndStatusIn(any(), any())).thenReturn(false);
        when(isikukoodParser.getAge(any())).thenReturn(25);
        when(scheduleGenerator.generateSchedule(any(), any())).thenReturn(Collections.emptyList());
        when(repository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(mapper.toResponse(any())).thenReturn(new LoanApplicationResponse());

        service.applyForLoan(request);

        verify(repository).save(argThat(entity -> entity.getStatus() == ApplicationStatus.IN_REVIEW));
    }

    @Test
    void shouldApproveApplication() {
        LoanApplication app = new LoanApplication();
        app.setStatus(ApplicationStatus.IN_REVIEW);

        when(repository.findById(1L)).thenReturn(Optional.of(app));
        when(repository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        service.approve(1L);

        assertThat(app.getStatus()).isEqualTo(ApplicationStatus.APPROVED);
    }

    @Test
    void shouldThrowWhenApprovingNonReviewApplication() {
        LoanApplication app = new LoanApplication();
        app.setStatus(ApplicationStatus.APPROVED);

        when(repository.findById(1L)).thenReturn(Optional.of(app));

        assertThatThrownBy(() -> service.approve(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Only IN_REVIEW applications can be approved");
    }
}
