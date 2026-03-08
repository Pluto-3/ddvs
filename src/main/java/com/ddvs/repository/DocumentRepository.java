package com.ddvs.repository;

import com.ddvs.entity.Document;
import com.ddvs.entity.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findByVerificationCode(String verificationCode);
    List<Document> findByIssuerId(Long issuerId);
    boolean existsByVerificationCode(String verificationCode);
}