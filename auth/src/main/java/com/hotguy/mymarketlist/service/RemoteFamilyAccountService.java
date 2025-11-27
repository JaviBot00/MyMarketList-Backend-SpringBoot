package com.hotguy.mymarketlist.service;

import com.hotguy.mymarketlist.dto.FamilyAccountResponse;
import com.hotguy.mymarketlist.exception.RemoteServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;


@Service
public class RemoteFamilyAccountService {

    private final WebClient webClient;

    // Puedes externalizar la URL base a application.properties si quieresq
    public RemoteFamilyAccountService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8082/api/family").build();
    }

//    public RemoteFamilyAccountService(WebClient.Builder builder,
//                                      @Value("${users.service.base-url:http://localhost:8082/api/family}") String baseUrl) {
//        this.webClient = builder.baseUrl(baseUrl).build();
//    }

    /**
     * Recupera la cuenta familiar por email desde users-service.
     * Blockea la llamada (sincrónica) — está bien para este uso simple.
     */


//    exchangeToMono te da acceso al statusCode() y al body antes de mapearlo.
    public FamilyAccountResponse login(String email) {
//        var payload = new java.util.HashMap<String, String>();
        var payload = new HashMap<String, String>();
        payload.put("email", email);

        return webClient.post()
                .uri("/login")
                .bodyValue(payload)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(FamilyAccountResponse.class);
                    } else {
                        // Leemos el body como String para reenviarlo o envolverlo
                        return response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RemoteServiceException(response.statusCode(), body)));
                    }
                })
                .block();
    }

    /**
     * Método auxiliar si quieres exponer más llamadas (p. ej. register).
     */
    public FamilyAccountResponse register(String email, String rawPassword) {
        // Se asume que users-service tiene un endpoint POST /api/family/register
        // que recibe {email, password} y devuelve la entidad creada.
        var payload = new java.util.HashMap<String, String>();
        payload.put("email", email);
        payload.put("password", rawPassword);

        return webClient.post()
                .uri("/register")
                .bodyValue(payload)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful() || response.statusCode().equals(HttpStatus.CREATED)) {
                        return response.bodyToMono(FamilyAccountResponse.class);
                    } else {
                        return response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RemoteServiceException(response.statusCode(), body)));
                    }
                })
                .block();
    }
}