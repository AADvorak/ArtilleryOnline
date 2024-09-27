package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.battle.command.DebugCommand;
import com.github.aadvorak.artilleryonline.collection.UserBattleMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DebugCommandService {

    private final UserBattleMap userBattleMap;

    public void addCommand(String userKey, DebugCommand debugCommand) {
        var battle = userBattleMap.get(userKey);
        if (battle == null) {
            return;
        }
        battle.getDebugCommands().add(debugCommand);
    }
}
