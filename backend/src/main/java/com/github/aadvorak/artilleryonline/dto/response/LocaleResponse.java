package com.github.aadvorak.artilleryonline.dto.response;

import com.github.aadvorak.artilleryonline.model.Locale;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class LocaleResponse {

    private String code;

    private Map<String, String> params;

    public static LocaleResponse of(Locale locale) {
        return new LocaleResponse()
                .setCode(locale.getCode().getValue())
                .setParams(locale.getParams());
    }
}
