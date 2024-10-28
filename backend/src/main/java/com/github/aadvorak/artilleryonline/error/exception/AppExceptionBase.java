package com.github.aadvorak.artilleryonline.error.exception;

public class AppExceptionBase extends RuntimeException {
    public AppExceptionBase() {
        super();
    }

    public AppExceptionBase(String message) {
        super(message);
    }
}
