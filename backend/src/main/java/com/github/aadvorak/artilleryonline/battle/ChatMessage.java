package com.github.aadvorak.artilleryonline.battle;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ChatMessage {

    private final String id = UUID.randomUUID().toString();

    private final long createTime = System.currentTimeMillis();

    private String text;

    private String nickname;
}
