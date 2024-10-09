package com.github.aadvorak.artilleryonline.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotEmpty
    private String email;

    @NotEmpty
    private String password;
}
