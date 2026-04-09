package com.example.lap.repository;

import com.example.lap.entity.ApplicationStatus;
import com.example.lap.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    boolean existsByPersonalCodeAndStatusIn(String personalCode, List<ApplicationStatus> statuses);
}