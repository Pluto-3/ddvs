package com.ddvs.controller;

import com.ddvs.entity.Document;
import com.ddvs.repository.DocumentRepository;
import com.ddvs.util.QRCodeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/qr")
@RequiredArgsConstructor
public class QRCodeController {

    private final QRCodeUtil qrCodeUtil;
    private final DocumentRepository documentRepository;

    @GetMapping("/{verificationCode}")
    public ResponseEntity<byte[]> getQRCode(@PathVariable String verificationCode) {
        documentRepository.findByVerificationCode(verificationCode)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        String verificationUrl = "https://verify.gov.tz/" + verificationCode;

        try {
            byte[] qrCode = qrCodeUtil.generateQRCode(verificationUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDispositionFormData("inline", verificationCode + ".png");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(qrCode);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code: " + e.getMessage());
        }
    }
}