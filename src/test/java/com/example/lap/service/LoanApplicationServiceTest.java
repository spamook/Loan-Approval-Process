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

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
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
    void testApplyForLoan_CustomerTooOld() {
        LoanApplicationRequest req = new LoanApplicationRequest();
        req.setPersonalCode("35001010001");
        
        when(repository.existsByPersonalCodeAndStatusIn(any(), any())).thenReturn(false);
        when(isikukoodParser.getAge(req.getPersonalCode())).thenReturn(75);
        
        LoanApplication mockSaved = LoanApplication.builder().status(ApplicationStatus.REJECTED).rejectionReason("CUSTOMER_TOO_OLD").build();
        when(repository.save(any())).thenReturn(mockSaved);
        when(mapper.toResponse(any())).thenReturn(LoanApplicationResponse.builder().status(ApplicationStatus.REJECTED).build());
        
        LoanApplicationResponse resp = service.applyForLoan(req);
        
        assertThat(resp.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
        verify(repository).save(argThat(app -> app.getStatus() == ApplicationStatus.REJECTED && "CUSTOMER_TOO_OLD".equals(app.getRejectionReason())));
        verifyNoInteractions(scheduleGenerator);
    }
    
    @Test
    void testApplyForLoan_Success() {
        LoanApplicationRequest req = new LoanApplicationRequest();
        req.setPersonalCode("39001010001");
        
        when(repository.existsByPersonalCodeAndStatusIn(any(), any())).thenReturn(false);
        when(isikukoodParser.getAge(req.getPersonalCode())).thenReturn(30);
        when(scheduleGenerator.generateSchedule(any(), any())).thenReturn(new ArrayList<>());
        
        LoanApplication mockSaved = LoanApplication.builder().status(ApplicationStatus.IN_REVIEW).build();
        when(repository.save(any())).thenReturn(mockSaved);
        when(mapper.toResponse(any())).thenReturn(LoanApplicationResponse.builder().status(ApplicationStatus.IN_REVIEW).build());
        
        LoanApplicationResponse resp = service.applyForLoan(req);
        
        assertThat(resp.getStatus()).isEqualTo(ApplicationStatus.IN_REVIEW);
        verify(scheduleGenerator).generateSchedule(any(), any());
    }
    
    @Test
    void testApprove_Success() {
        LoanApplication app = LoanApplication.builder().status(ApplicationStatus.IN_REVIEW).build();
        when(repository.findById(1L)).thenReturn(Optional.of(app));
        when(repository.save(any())).thenReturn(app);
        
        service.approve(1L);
        assertThat(app.getStatus()).isEqualTo(ApplicationStatus.APPROVED);
    }
}
