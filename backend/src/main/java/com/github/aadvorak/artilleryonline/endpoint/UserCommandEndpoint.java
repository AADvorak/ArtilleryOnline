package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.battle.command.UserCommand;
import com.github.aadvorak.artilleryonline.service.UserCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/battles/commands")
@RequiredArgsConstructor
public class UserCommandEndpoint {

    private final UserCommandService userCommandService;

    @PostMapping
    public void addCommand(@CookieValue("UserKey") String userKey, @RequestBody UserCommand userCommand) {
        userCommandService.addCommand(userKey, userCommand);
    }
}
