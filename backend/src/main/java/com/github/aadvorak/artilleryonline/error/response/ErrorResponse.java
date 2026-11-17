package com.github.aadvorak.artilleryonline.error.response;

import com.github.aadvorak.artilleryonline.dto.response.LocaleResponse;
import com.github.aadvorak.artilleryonline.error.exception.ConflictAppException;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class ErrorResponse {

    private String code;

    private String message;

    private LocaleResponse locale;

    private Object params;

    private List<ValidationResponse> validation;

    public static ErrorResponse of(ConflictAppException e) {
        return new ErrorResponse()
                .setCode("ConflictAppException")
                .setMessage(e.getMessage())
                .setLocale(e.getLocale() != null ? LocaleResponse.of(e.getLocale()) : null);
    }
}
