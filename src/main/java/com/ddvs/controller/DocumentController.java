package com.ddvs.controller;

import com.ddvs.dto.request.DocumentRequest;
import com.ddvs.dto.request.RevokeRequest;
import com.ddvs.dto.response.DocumentResponse;
import com.ddvs.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ISSUER')")
    public ResponseEntity<DocumentResponse> issue(@RequestBody DocumentRequest request) {
        return ResponseEntity.ok(documentService.issue(request));
    }

    @GetMapping
    // @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<List<DocumentResponse>> getAll() {
        return ResponseEntity.ok(documentService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ISSUER', 'AUDITOR')")
    public ResponseEntity<DocumentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getById(id));
    }

    @GetMapping("/issuer/{issuerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ISSUER', 'AUDITOR')")
    public ResponseEntity<List<DocumentResponse>> getByIssuer(@PathVariable Long issuerId) {
        return ResponseEntity.ok(documentService.getByIssuer(issuerId));
    }

    @PutMapping("/{id}/revoke")
    @PreAuthorize("hasAnyRole('ADMIN', 'ISSUER')")
    public ResponseEntity<DocumentResponse> revoke(@PathVariable Long id,
                                                   @RequestBody RevokeRequest request) {
        return ResponseEntity.ok(documentService.revoke(id, request));
    }
}