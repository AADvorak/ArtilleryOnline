package com.github.aadvorak.artilleryonline.ws;

import com.github.aadvorak.artilleryonline.battle.command.UserCommand;
import com.github.aadvorak.artilleryonline.service.UserCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class UserCommandController {

    private final UserCommandService userCommandService;

    @MessageMapping("/battle/commands")
    public void addCommand(UserCommand userCommand) {
        userCommandService.addCommand(userCommand);
    }
}
