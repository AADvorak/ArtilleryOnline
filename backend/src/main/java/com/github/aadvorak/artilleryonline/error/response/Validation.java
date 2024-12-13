package com.github.aadvorak.artilleryonline.error.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Validation {
    WRONG("wrong", "Wrong value"),
    EXISTS("exists", "Already exists");

    private final String code;
    private final String message;
}
