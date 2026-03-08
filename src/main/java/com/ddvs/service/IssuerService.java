package com.ddvs.service;

import com.ddvs.dto.request.IssuerRequest;
import com.ddvs.dto.response.IssuerResponse;
import com.ddvs.entity.Issuer;
import com.ddvs.repository.IssuerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IssuerService {

    private final IssuerRepository issuerRepository;

    public IssuerResponse create(IssuerRequest request) {
        Issuer issuer = Issuer.builder()
                .name(request.getName())
                .organizationType(request.getOrganizationType())
                .contactEmail(request.getContactEmail())
                .build();

        return toResponse(issuerRepository.save(issuer));
    }

    public List<IssuerResponse> getAll() {
        return issuerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public IssuerResponse getById(Long id) {
        return toResponse(findById(id));
    }

    public IssuerResponse update(Long id, IssuerRequest request) {
        Issuer issuer = findById(id);
        issuer.setName(request.getName());
        issuer.setOrganizationType(request.getOrganizationType());
        issuer.setContactEmail(request.getContactEmail());
        return toResponse(issuerRepository.save(issuer));
    }

    public void delete(Long id) {
        issuerRepository.delete(findById(id));
    }

    private Issuer findById(Long id) {
        return issuerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issuer not found with id: " + id));
    }

    private IssuerResponse toResponse(Issuer issuer) {
        return new IssuerResponse(
                issuer.getId(),
                issuer.getName(),
                issuer.getOrganizationType(),
                issuer.getContactEmail(),
                issuer.getCreatedAt()
        );
    }
}