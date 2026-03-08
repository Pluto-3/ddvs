package com.ddvs.service;

import com.ddvs.dto.response.VerificationResponse;
import com.ddvs.entity.Document;
import com.ddvs.entity.DocumentStatus;
import com.ddvs.entity.VerificationLog;
import com.ddvs.repository.DocumentRepository;
import com.ddvs.repository.VerificationLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final DocumentRepository documentRepository;
    private final VerificationLogRepository verificationLogRepository;
    private final DocumentService documentService;

    public VerificationResponse verify(String verificationCode, HttpServletRequest request) {
        String ipAddress = extractIp(request);

        Optional<Document> optional = documentRepository.findByVerificationCode(verificationCode);

        if (optional.isEmpty()) {
            logAttempt(verificationCode, ipAddress, "NOT_FOUND");
            return new VerificationResponse(
                    verificationCode,
                    null, null, null, null, null, null,
                    "NOT_FOUND",
                    "No document found with this verification code."
            );
        }

        Document document = optional.get();

        // Auto-expire check
        documentService.checkAndExpireDocument(document);

        String status = document.getStatus().name();
        String message = resolveMessage(document.getStatus());

        logAttempt(verificationCode, ipAddress, status);

        return new VerificationResponse(
                document.getVerificationCode(),
                document.getDocumentType(),
                document.getTitle(),
                document.getOwnerName(),
                document.getIssuer().getName(),
                document.getIssuedDate(),
                document.getExpirationDate(),
                status,
                message
        );
    }

    public List<VerificationLog> getLogs() {
        return verificationLogRepository.findAll();
    }

    private void logAttempt(String code, String ip, String result) {
        VerificationLog log = VerificationLog.builder()
                .verificationCode(code)
                .ipAddress(ip)
                .result(result)
                .build();
        verificationLogRepository.save(log);
    }

    private String resolveMessage(DocumentStatus status) {
        return switch (status) {
            case VALID -> "This document is valid and authentic.";
            case EXPIRED -> "This document has expired.";
            case REVOKED -> "This document has been revoked and is no longer valid.";
        };
    }

    private String extractIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}