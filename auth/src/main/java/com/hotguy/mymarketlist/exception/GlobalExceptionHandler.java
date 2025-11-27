package com.hotguy.mymarketlist.exception;

import com.hotguy.mymarketlist.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Maneja las excepciones lanzadas por el remote service y reenvía status + body
    @ExceptionHandler(RemoteServiceException.class)
    public ResponseEntity<String> handleRemote(RemoteServiceException ex) {
        // body ya es el texto/json que devolvió el otro micro
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getBody());
    }

    // Para errores de negocio locales
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    // Manejo de validaciones
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        return ResponseEntity.badRequest().body(errors);
    }
}
