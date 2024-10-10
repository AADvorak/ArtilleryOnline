package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.battle.BattleStage;
import com.github.aadvorak.artilleryonline.battle.command.UserCommand;
import com.github.aadvorak.artilleryonline.collection.UserBattleMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final UserBattleMap userBattleMap;
    private final UserService userService;

    public void addCommand(UserCommand userCommand) {
        var nickname = userService.getUserFromContext().getNickname();
        var battle = userBattleMap.get(nickname);
        if (battle == null || !BattleStage.ACTIVE.equals(battle.getBattleStage())) {
            return;
        }
        battle.getUserCommandQueues().get(nickname).add(userCommand);
    }
}
