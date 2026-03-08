package com.ddvs.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class IssuerResponse {
    private Long id;
    private String name;
    private String organizationType;
    private String contactEmail;
    private LocalDateTime createdAt;
}