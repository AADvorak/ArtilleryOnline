package com.github.aadvorak.artilleryonline.dto.response;

import com.github.aadvorak.artilleryonline.battle.ChatMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Setter
@Builder
public class ChatMessageResponse {

    private String id;

    private LocalDateTime time;

    private String text;

    private String nickname;

    public static ChatMessageResponse of(ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
                .id(chatMessage.getId())
                .time(LocalDateTime.ofInstant(Instant.ofEpochMilli(chatMessage.getCreateTime()),
                        ZoneId.systemDefault()))
                .text(chatMessage.getText())
                .nickname(chatMessage.getNickname())
                .build();
    }
}
