package com.github.aadvorak.artilleryonline.error.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ValidationResponse {

    private String code;

    private String field;

    private String message;

    public ValidationResponse setValidation(Validation validation) {
        code = validation.getCode();
        message = validation.getMessage();
        return this;
    }
}
