package com.ddvs.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class VerificationCodeGenerator {

    private static final String COUNTRY_CODE = "TZ";
    private static final Random random = new Random();

    public String generate(String documentType) {
        String sector = resolveSector(documentType);
        String digits = String.format("%05d", random.nextInt(100000));
        return COUNTRY_CODE + "-" + sector + "-" + digits;
    }

    private String resolveSector(String documentType) {
        if (documentType == null) return "GEN";
        return switch (documentType.toUpperCase()) {
            case "CERTIFICATE" -> "EDU";
            case "TAX_CLEARANCE" -> "TAX";
            case "LICENSE" -> "LIC";
            case "PERMIT" -> "PRM";
            case "IMMIGRATION" -> "IMG";
            default -> "GEN";
        };
    }
}