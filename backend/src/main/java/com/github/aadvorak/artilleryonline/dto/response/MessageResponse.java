package com.github.aadvorak.artilleryonline.dto.response;

import com.github.aadvorak.artilleryonline.model.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MessageResponse {

    private String id;

    private String text;

    public static MessageResponse of(Message message) {
        return new MessageResponse()
                .setId(message.getId())
                .setText(message.getText());
    }
}
