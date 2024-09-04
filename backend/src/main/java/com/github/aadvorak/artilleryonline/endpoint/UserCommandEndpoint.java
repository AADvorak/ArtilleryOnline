package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.battle.command.UserCommand;
import com.github.aadvorak.artilleryonline.service.UserCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

// todo remove
@CrossOrigin(origins = "http://localhost:5173/", maxAge = 3600)
@RestController
@RequestMapping("/api/battles/commands")
@RequiredArgsConstructor
public class UserCommandEndpoint {

    private final UserCommandService userCommandService;

    @PostMapping
    public void addCommand(@RequestHeader("UserKey") String userKey, @RequestBody UserCommand userCommand) {
        userCommandService.addCommand(userKey, userCommand);
    }
}
