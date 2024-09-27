package com.github.aadvorak.artilleryonline.ws;

import com.github.aadvorak.artilleryonline.battle.command.UserKeyAndDebugCommand;
import com.github.aadvorak.artilleryonline.service.DebugCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class DebugCommandController {

    private final DebugCommandService debugCommandService;

    @MessageMapping("/debug-commands")
    public void addCommand(UserKeyAndDebugCommand userKeyAndCommand) {
        debugCommandService.addCommand(userKeyAndCommand.getUserKey(), userKeyAndCommand.getDebugCommand());
    }
}
