package com.github.aadvorak.artilleryonline.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageRequest {

    @NotEmpty
    @Size(min = 1, max = 300)
    private String text;
}
