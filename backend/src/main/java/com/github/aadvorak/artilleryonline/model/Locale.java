package com.github.aadvorak.artilleryonline.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class Locale {

    private LocaleCode code;

    private Map<String, String> params;
}
