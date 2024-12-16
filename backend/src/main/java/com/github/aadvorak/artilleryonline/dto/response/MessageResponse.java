package com.github.aadvorak.artilleryonline.dto.response;

import com.github.aadvorak.artilleryonline.model.Message;
import com.github.aadvorak.artilleryonline.model.MessageSpecial;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Setter
@Accessors(chain = true)
public class MessageResponse {

    private String id;

    private LocalDateTime time;

    private String text;

    private LocaleResponse locale;

    private MessageSpecial special;

    public static MessageResponse of(Message message) {
        return new MessageResponse()
                .setId(message.getId())
                .setText(message.getText())
                .setLocale(message.getLocale() != null ? LocaleResponse.of(message.getLocale()) : null)
                .setSpecial(message.getSpecial())
                .setTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(message.getCreateTime()),
                        ZoneId.systemDefault()));
    }
}
