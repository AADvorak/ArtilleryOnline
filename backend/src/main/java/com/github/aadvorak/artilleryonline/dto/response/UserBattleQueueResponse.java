package com.github.aadvorak.artilleryonline.dto.response;

import com.github.aadvorak.artilleryonline.collection.UserBattleQueueParams;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
public class UserBattleQueueResponse {

    private LocalDateTime addTime;

    private UserBattleQueueParams params;
}
