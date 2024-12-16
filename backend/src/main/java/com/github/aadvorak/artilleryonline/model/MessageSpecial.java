package com.github.aadvorak.artilleryonline.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MessageSpecial {

    private UserBattleResult userBattleResult;
}
