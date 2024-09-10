package com.github.aadvorak.artilleryonline.ws;

import com.github.aadvorak.artilleryonline.battle.command.UserKeyAndCommand;
import com.github.aadvorak.artilleryonline.service.UserCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class UserCommandController {

    private final UserCommandService userCommandService;

    @MessageMapping("/commands")
    public void addCommand(UserKeyAndCommand userKeyAndCommand) {
        userCommandService.addCommand(userKeyAndCommand.getUserKey(), userKeyAndCommand.getUserCommand());
    }
}
