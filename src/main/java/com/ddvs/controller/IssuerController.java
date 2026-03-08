package com.ddvs.controller;

import com.ddvs.dto.request.IssuerRequest;
import com.ddvs.dto.response.IssuerResponse;
import com.ddvs.service.IssuerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/issuers")
@RequiredArgsConstructor
public class IssuerController {

    private final IssuerService issuerService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IssuerResponse> create(@RequestBody IssuerRequest request) {
        return ResponseEntity.ok(issuerService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<List<IssuerResponse>> getAll() {
        return ResponseEntity.ok(issuerService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<IssuerResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(issuerService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IssuerResponse> update(@PathVariable Long id,
                                                 @RequestBody IssuerRequest request) {
        return ResponseEntity.ok(issuerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        issuerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}