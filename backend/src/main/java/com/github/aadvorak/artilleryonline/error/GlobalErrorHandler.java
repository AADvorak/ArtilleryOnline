package com.github.aadvorak.artilleryonline.error;

import com.github.aadvorak.artilleryonline.dto.response.LocaleResponse;
import com.github.aadvorak.artilleryonline.error.exception.AuthenticationAppException;
import com.github.aadvorak.artilleryonline.error.exception.BadRequestAppException;
import com.github.aadvorak.artilleryonline.error.exception.ConflictAppException;
import com.github.aadvorak.artilleryonline.error.response.ErrorResponse;
import com.github.aadvorak.artilleryonline.error.response.ValidationResponse;
import com.github.aadvorak.artilleryonline.error.exception.NotFoundAppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalErrorHandler {

    @ExceptionHandler(NotFoundAppException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNotFoundAppException() {
    }

    @ExceptionHandler(ConflictAppException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictAppException(ConflictAppException exc) {
        return new ErrorResponse()
                .setCode("ConflictAppException")
                .setMessage(exc.getMessage())
                .setLocale(exc.getLocale() != null ? LocaleResponse.of(exc.getLocale()) : null);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ErrorResponse handleBindException(BindException exc) {
        return new ErrorResponse()
                .setCode("BindException")
                .setMessage(exc.getMessage())
                .setValidation(makeValidation(exc));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException exc) {
        return new ErrorResponse()
                .setCode("MethodArgumentNotValidException")
                .setMessage(exc.getMessage())
                .setValidation(makeValidation(exc));
    }

    @ExceptionHandler(BadRequestAppException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestAppException(BadRequestAppException exc) {
        return new ErrorResponse()
                .setCode("BadRequestAppException")
                .setValidation(exc.getValidation());
    }

    @ExceptionHandler(AuthenticationAppException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleBadRequestAppException() {
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception exc) {
        log.error(exc.getMessage(), exc);
        return new ErrorResponse()
                .setCode("Exception")
                .setMessage(exc.getMessage());
    }

    private List<ValidationResponse> makeValidation(BindException exc) {
        List<ValidationResponse> errors = new ArrayList<>();
        exc.getBindingResult().getFieldErrors().forEach(fieldError ->
                errors.add(new ValidationResponse()
                        .setCode(fieldError.getCode())
                        .setField(fieldError.getField())
                        .setMessage(fieldError.getDefaultMessage())));
        exc.getBindingResult().getGlobalErrors().forEach(err ->
                errors.add(new ValidationResponse()
                        .setCode(err.getCode())
                        .setField(err.getObjectName())
                        .setMessage(err.getDefaultMessage())));
        return errors;
    }
}
