package com.github.aadvorak.artilleryonline.error;

import com.github.aadvorak.artilleryonline.error.exception.NotFoundAppException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(NotFoundAppException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNotFoundAppException() {
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Map<String, String> handleException(Exception e) {
        return Map.of("message", e.getMessage());
    }
}
