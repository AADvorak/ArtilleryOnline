package com.github.aadvorak.artilleryonline.error.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Validation {
    WRONG("WRONG", "Wrong value"),
    EXISTS("EXISTS", "Already exists");

    private final String code;
    private final String message;
}
