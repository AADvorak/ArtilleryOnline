package com.github.aadvorak.artilleryonline.error.response;

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

    private Object params;

    private List<ValidationResponse> validation;
}
