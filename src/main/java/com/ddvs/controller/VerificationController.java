package com.ddvs.controller;

import com.ddvs.dto.response.VerificationResponse;
import com.ddvs.entity.VerificationLog;
import com.ddvs.service.VerificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @GetMapping("/verify/{verificationCode}")
    public ResponseEntity<VerificationResponse> verify(
            @PathVariable String verificationCode,
            HttpServletRequest request) {
        return ResponseEntity.ok(verificationService.verify(verificationCode, request));
    }

    @GetMapping("/verification-logs")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<List<VerificationLog>> getLogs() {
        return ResponseEntity.ok(verificationService.getLogs());
    }
}