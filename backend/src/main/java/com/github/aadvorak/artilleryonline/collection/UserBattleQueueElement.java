package com.github.aadvorak.artilleryonline.collection;

import com.github.aadvorak.artilleryonline.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserBattleQueueElement {

    private final Long addTime = System.currentTimeMillis();

    private User user;

    private UserBattleQueueParams params;
}
