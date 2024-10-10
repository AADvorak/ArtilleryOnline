package com.github.aadvorak.artilleryonline.error;

import com.github.aadvorak.artilleryonline.error.exception.BadRequestAppException;
import com.github.aadvorak.artilleryonline.error.response.ErrorResponse;
import com.github.aadvorak.artilleryonline.error.response.ValidationResponse;
import com.github.aadvorak.artilleryonline.error.exception.NotFoundAppException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(NotFoundAppException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNotFoundAppException() {
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
    public ErrorResponse handleBadCredentialsException(BadRequestAppException exc) {
        return new ErrorResponse()
                .setCode("BadRequestAppException")
                .setValidation(exc.getValidation());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception exc) {
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
