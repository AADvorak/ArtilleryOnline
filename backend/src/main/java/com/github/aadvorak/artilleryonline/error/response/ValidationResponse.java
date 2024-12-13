package com.github.aadvorak.artilleryonline.error.response;

import com.github.aadvorak.artilleryonline.dto.response.LocaleResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ValidationResponse {

    private LocaleResponse locale;

    private String field;

    private String message;

    public ValidationResponse setValidation(Validation validation) {
        locale = new LocaleResponse().setCode(validation.getCode());
        message = validation.getMessage();
        return this;
    }
}
