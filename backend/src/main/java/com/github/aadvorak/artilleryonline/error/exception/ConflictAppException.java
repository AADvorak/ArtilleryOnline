package com.github.aadvorak.artilleryonline.error.exception;

import com.github.aadvorak.artilleryonline.model.Locale;
import lombok.Getter;

@Getter
public class ConflictAppException extends AppExceptionBase {

    private final Locale locale;

    public ConflictAppException(String message, Locale locale) {
        super(message);
        this.locale = locale;
    }
}
