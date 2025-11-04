package com.hotguy.mymarketlist.security;

import com.hotguy.mymarketlist.service.RemoteFamilyAccountService;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;


@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RemoteFamilyAccountService remoteFamilyAccountService;


    public JwtFilter(JwtUtil jwtUtil, RemoteFamilyAccountService remoteFamilyAccountService) {
        this.jwtUtil = jwtUtil;
        this.remoteFamilyAccountService = remoteFamilyAccountService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

//        String token = obtenerToken(request);
//        if (token != null) {
//            String username = jwtUtil.extraerUsername(token); // esto lanza si el token está caducado
//
//            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                UserDetails userDetails = profileService.loadUserByUsername(username);
//
//                if (jwtUtil.validarToken(token, username)) {
//                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
//                            userDetails, null, userDetails.getAuthorities()
//                    );
//                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(auth);
//                }
//            }
//        }
//
//        filterChain.doFilter(request, response);

        try {
            String token = obtenerToken(request);
            if (token != null) {
                String email = jwtUtil.extraerUsername(token);

                if (email != null && org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Cargamos info mínima del usuario (password no es necesaria aquí)
                    // Para construir UserDetails sin contraseña usamos roles por defecto.
                    // Si quisieras las autoridades reales, consulta users-service y mapea.
                    UserDetails userDetails;
                    try {
                        var family = remoteFamilyAccountService.getByEmail(email);
                        userDetails = User.withUsername(family.getEmail())
                                .password(family.getPassword())
                                .authorities("ROLE_USER")
                                .build();
                    } catch (UsernameNotFoundException e) {
                        userDetails = User.withUsername(email)
                                .password("") // no needed here
                                .authorities(Collections.emptyList())
                                .build();
                    }

                    if (jwtUtil.validarToken(token, email)) {
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            }

        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            // Token expirado -> 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Token expirado\"}");
            return;
        } catch (Exception ex) {
            // token inválido u otro fallo -> no autenticar
        }

        filterChain.doFilter(request, response);

//        } catch (ExpiredJwtException e) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.setContentType("application/json");
//            response.getWriter().write("{\"error\": \"Token expirado. Por favor, vuelve a iniciar sesión.\"}");
//        } catch (JwtException | IllegalArgumentException e) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.setContentType("application/json");
//            response.getWriter().write("{\"error\": \"Token inválido.\"}");
//        }
    }

    private String obtenerToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}