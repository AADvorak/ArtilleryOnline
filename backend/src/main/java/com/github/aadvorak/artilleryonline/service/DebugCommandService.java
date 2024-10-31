package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.battle.command.DebugCommand;
import com.github.aadvorak.artilleryonline.collection.UserBattleMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DebugCommandService {

    private final UserBattleMap userBattleMap;
    private final UserService userService;

    public void addCommand(DebugCommand debugCommand) {
        var user = userService.getUserFromContext();
        var battle = userBattleMap.get(user.getId());
        if (battle == null) {
            return;
        }
        battle.getQueues().getDebugCommands().add(debugCommand);
    }
}
