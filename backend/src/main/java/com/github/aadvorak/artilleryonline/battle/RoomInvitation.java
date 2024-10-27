package com.github.aadvorak.artilleryonline.battle;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
public class RoomInvitation {

    private final String id = UUID.randomUUID().toString();

    private final long createTime = System.currentTimeMillis();

    private Room room;

    private Long userId;
}
