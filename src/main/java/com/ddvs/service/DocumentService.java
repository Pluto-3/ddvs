package com.ddvs.service;

import com.ddvs.client.NotificationClient;
import com.ddvs.dto.request.DocumentRequest;
import com.ddvs.dto.request.RevokeRequest;
import com.ddvs.dto.response.DocumentResponse;
import com.ddvs.entity.*;
import com.ddvs.repository.DocumentRepository;
import com.ddvs.repository.IssuerRepository;
import com.ddvs.repository.UserRepository;
import com.ddvs.util.VerificationCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final IssuerRepository issuerRepository;
    private final UserRepository userRepository;
    private final VerificationCodeGenerator codeGenerator;
    private final NotificationClient notificationClient;

    public DocumentResponse issue(DocumentRequest request) {
        Issuer issuer = issuerRepository.findById(request.getIssuerId())
                .orElseThrow(() -> new RuntimeException("Issuer not found"));

        String currentUserEmail = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String code;
        do {
            code = codeGenerator.generate(request.getDocumentType());
        } while (documentRepository.existsByVerificationCode(code));

        Document document = Document.builder()
                .title(request.getTitle())
                .documentType(request.getDocumentType())
                .ownerName(request.getOwnerName())
                .verificationCode(code)
                .issuer(issuer)
                .expirationDate(request.getExpirationDate())
                .status(DocumentStatus.VALID)
                .createdBy(currentUser)
                .build();

        Document saved = documentRepository.save(document);

        if (request.getOwnerEmail() != null && !request.getOwnerEmail().isBlank()) {
            notificationClient.send(
                    request.getOwnerEmail(),
                    "Your document has been issued",
                    "document_issued",
                    Map.of(
                            "name", saved.getOwnerName(),
                            "documentName", saved.getTitle(),
                            "verificationCode", saved.getVerificationCode(),
                            "issuedBy", issuer.getName()
                    )
            );
        }

        return toResponse(saved);
    }

    public List<DocumentResponse> getAll() {
        return documentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public DocumentResponse getById(Long id) {
        return toResponse(findById(id));
    }

    public List<DocumentResponse> getByIssuer(Long issuerId) {
        return documentRepository.findByIssuerId(issuerId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DocumentResponse revoke(Long id, RevokeRequest request) {
        Document document = findById(id);

        String currentUserEmail = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        document.setStatus(DocumentStatus.REVOKED);
        document.setRevokedBy(currentUserEmail);
        document.setRevokedReason(request.getReason());

        Document saved = documentRepository.save(document);

        if (request.getOwnerEmail() != null && !request.getOwnerEmail().isBlank()) {
            notificationClient.send(
                    request.getOwnerEmail(),
                    "Your document has been revoked",
                    "document_revoked",
                    Map.of(
                            "name", saved.getOwnerName(),
                            "documentName", saved.getTitle(),
                            "reason", request.getReason()
                    )
            );
        }

        return toResponse(saved);
    }

    public void checkAndExpireDocument(Document document) {
        if (document.getExpirationDate() != null &&
                document.getExpirationDate().isBefore(LocalDate.now()) &&
                document.getStatus() == DocumentStatus.VALID) {
            document.setStatus(DocumentStatus.EXPIRED);
            documentRepository.save(document);
        }
    }

    private Document findById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));
    }

    public DocumentResponse toResponse(Document doc) {
        return new DocumentResponse(
                doc.getId(),
                doc.getTitle(),
                doc.getDocumentType(),
                doc.getOwnerName(),
                doc.getVerificationCode(),
                doc.getIssuer().getName(),
                doc.getIssuedDate(),
                doc.getExpirationDate(),
                doc.getStatus().name(),
                doc.getCreatedBy() != null ? doc.getCreatedBy().getName() : null,
                doc.getRevokedBy(),
                doc.getRevokedReason(),
                doc.getCreatedAt()
        );
    }
}