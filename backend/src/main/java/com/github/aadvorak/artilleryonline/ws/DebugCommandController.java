package com.github.aadvorak.artilleryonline.ws;

import com.github.aadvorak.artilleryonline.battle.command.DebugCommand;
import com.github.aadvorak.artilleryonline.properties.ApplicationSettings;
import com.github.aadvorak.artilleryonline.service.DebugCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class DebugCommandController {

    private final DebugCommandService debugCommandService;
    private final ApplicationSettings applicationSettings;

    @MessageMapping("/battle/debug-commands")
    public void addCommand(DebugCommand debugCommand) {

        if (applicationSettings.isDebug()) {
            debugCommandService.addCommand(debugCommand);
        }
    }
}
