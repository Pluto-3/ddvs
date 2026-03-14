package com.ddvs.dto.request;

import lombok.Data;

@Data
public class RevokeRequest {
    private String reason;
    private String ownerEmail;
}