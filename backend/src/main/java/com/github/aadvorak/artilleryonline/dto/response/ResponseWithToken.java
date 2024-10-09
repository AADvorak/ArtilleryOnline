package com.github.aadvorak.artilleryonline.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ResponseWithToken<T> {

    private T response;

    private String token;
}
