package com.github.aadvorak.artilleryonline.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
public class Message {

    private final String id = UUID.randomUUID().toString();

    private final long createTime = System.currentTimeMillis();

    private Long userId;

    private String text;

    private Locale locale;

    private MessageSpecial special;
}
