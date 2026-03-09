package com.ddvs.controller;

import com.ddvs.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping("/{verificationCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ISSUER')")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable String verificationCode) {
        try {
            byte[] pdf = certificateService.generateCertificate(verificationCode);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", verificationCode + "-certificate.pdf");

            return ResponseEntity.ok().headers(headers).body(pdf);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate certificate: " + e.getMessage());
        }
    }
}