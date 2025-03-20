package com.github.aadvorak.artilleryonline.error.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Validation {
    EMPTY("empty", "Empty value"),
    WRONG("wrong", "Wrong value"),
    EXISTS("exists", "Already exists");

    private final String code;
    private final String message;
}
