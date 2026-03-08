package com.ddvs.service;

import com.ddvs.dto.request.LoginRequest;
import com.ddvs.dto.request.RegisterRequest;
import com.ddvs.dto.response.AuthResponse;
import com.ddvs.entity.Issuer;
import com.ddvs.entity.Role;
import com.ddvs.entity.User;
import com.ddvs.repository.IssuerRepository;
import com.ddvs.repository.UserRepository;
import com.ddvs.security.UserDetailsImpl;
import com.ddvs.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final IssuerRepository issuerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        Issuer issuer = null;
        if (request.getIssuerId() != null) {
            issuer = issuerRepository.findById(request.getIssuerId())
                    .orElseThrow(() -> new RuntimeException("Issuer not found"));
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole().toUpperCase()))
                .issuer(issuer)
                .build();

        userRepository.save(user);

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(token, user.getRole().name(), user.getName());
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(token, userDetails.getRole(), userDetails.getUsername());
    }
}