package com.github.aadvorak.artilleryonline.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditUserRequest {

    @Email
    @NotEmpty
    @Size(min = 5, max = 20)
    private String email;

    @NotEmpty
    @Size(min = 5, max = 20)
    private String nickname;
}
