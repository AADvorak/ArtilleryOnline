package com.github.aadvorak.artilleryonline.error.exception;

import com.github.aadvorak.artilleryonline.error.response.ValidationResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class BadRequestAppException extends AppExceptionBase {
    private final List<ValidationResponse> validation;
}
