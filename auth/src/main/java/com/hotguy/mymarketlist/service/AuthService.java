package com.hotguy.mymarketlist.service;

import com.hotguy.mymarketlist.dto.AuthResponse;
import com.hotguy.mymarketlist.dto.FamilyAccountResponse;
import com.hotguy.mymarketlist.dto.RegisterRequest;
import com.hotguy.mymarketlist.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final RemoteFamilyAccountService remoteService;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(RemoteFamilyAccountService remoteService, AuthenticationManager authManager, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.remoteService = remoteService;
        this.authManager = authManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // LOGIN -> valida contra users-service y devuelve token si ok
    @Transactional
    public AuthResponse login(String email, String rawPassword) {
        FamilyAccountResponse family = remoteService.login(email);
        if (family == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        String encoded = family.getPassword(); // password hashed coming from users service
        if (!passwordEncoder.matches(rawPassword, encoded)) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // Generar token con subject = email (podrías incluir familyId o perfil activo si quieres)
        String token = jwtUtil.generarToken(family.getEmail());

        // Devuelve token directamente
        return new AuthResponse(token);
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        // 1. Crea el usuario en users-service
        FamilyAccountResponse created = remoteService.register(req.getEmail(), req.getPassword());

        // 2. Genera token con el email registrado
        String token = jwtUtil.generarToken(created.getEmail());

        // 3. Devuelve token directamente
        return new AuthResponse(token);
    }
}