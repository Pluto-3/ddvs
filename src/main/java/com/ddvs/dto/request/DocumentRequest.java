package com.ddvs.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DocumentRequest {
    private String title;
    private String documentType;
    private String ownerName;
    private String ownerEmail;
    private Long issuerId;
    private LocalDate expirationDate;
}