package com.github.aadvorak.artilleryonline.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {

    private long id;

    private String email;

    private String nickname;

    private String token;
}
