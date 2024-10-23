package com.github.aadvorak.artilleryonline.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSettingRequest {

    @NotEmpty
    private String name;

    @NotEmpty
    private String value;
}
