package com.appointment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException exception){
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            message = "Something went wrong.";
        }

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(message);
    }
}
