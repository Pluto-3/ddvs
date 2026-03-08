package com.ddvs.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DocumentResponse {
    private Long id;
    private String title;
    private String documentType;
    private String ownerName;
    private String verificationCode;
    private String issuerName;
    private LocalDate issuedDate;
    private LocalDate expirationDate;
    private String status;
    private String createdBy;
    private String revokedBy;
    private String revokedReason;
    private LocalDateTime createdAt;
}