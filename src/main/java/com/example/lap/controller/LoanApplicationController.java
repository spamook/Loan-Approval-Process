package com.example.lap.controller;

import com.example.lap.dto.LoanApplicationRequest;
import com.example.lap.dto.LoanApplicationResponse;
import com.example.lap.service.LoanApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/loan-applications")
@RequiredArgsConstructor
@Tag(name = "Loan Applications", description = "Endpoints for managing loan applications")
public class LoanApplicationController {

    private final LoanApplicationService service;

    @PostMapping
    @Operation(summary = "Apply for a new loan", description = "Submits a new loan application and returns the generated schedule or rejection")
    public ResponseEntity<LoanApplicationResponse> applyForLoan(@Valid @RequestBody LoanApplicationRequest request) {
        LoanApplicationResponse response = service.applyForLoan(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get application by ID", description = "Retrieves an application and its payment schedule")
    public ResponseEntity<LoanApplicationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve application", description = "Approves an IN_REVIEW application")
    public ResponseEntity<LoanApplicationResponse> approve(@PathVariable Long id) {
        return ResponseEntity.ok(service.approve(id));
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject application", description = "Rejects an IN_REVIEW application with a reason")
    public ResponseEntity<LoanApplicationResponse> reject(@PathVariable Long id, @RequestParam String reason) {
        return ResponseEntity.ok(service.reject(id, reason));
    }

    @PostMapping("/{id}/regenerate-schedule")
    @Operation(summary = "Regenerate schedule", description = "Regenerates the payment schedule for an IN_REVIEW application upon parameter changes")
    public ResponseEntity<LoanApplicationResponse> regenerateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody LoanApplicationRequest request) {
        return ResponseEntity.ok(service.regenerateSchedule(id, request));
    }
}
