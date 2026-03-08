package com.ddvs.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class VerificationResponse {

    private String verificationCode;
    private String documentType;
    private String title;
    private String ownerName;
    private String issuedBy;
    private LocalDate issuedDate;
    private LocalDate expirationDate;
    private String status;
    private String message;
}