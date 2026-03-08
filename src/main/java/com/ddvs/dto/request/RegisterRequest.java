package com.ddvs.dto.request;

import lombok.Data;
import lombok.Locked;

@Data
public class RegisterRequest {

    private String name;
    private String email;
    private String password;
    private String role;
    private Long issuerId;
}
