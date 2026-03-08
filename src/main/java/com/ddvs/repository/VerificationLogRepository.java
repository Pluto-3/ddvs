package com.ddvs.repository;

import com.ddvs.entity.VerificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VerificationLogRepository extends JpaRepository<VerificationLog, Long> {
    List<VerificationLog> findByVerificationCode(String verificationCode);
}