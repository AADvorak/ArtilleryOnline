package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.dto.request.UserSettingRequest;
import com.github.aadvorak.artilleryonline.dto.response.UserSettingResponse;
import com.github.aadvorak.artilleryonline.service.UserSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-settings/{groupName}")
@RequiredArgsConstructor
public class UserSettingEndpoint {

    private final UserSettingService userSettingService;

    @GetMapping()
    public List<UserSettingResponse> getSettingsFromGroup(@PathVariable String groupName) {
        return userSettingService.getSettingsFromGroup(groupName);
    }

    @PutMapping
    public void setSetting(@PathVariable String groupName, @RequestBody UserSettingRequest request) {
        userSettingService.setSetting(groupName, request);
    }

    @DeleteMapping
    public void clearSettingsFromGroup(@PathVariable String groupName) {
        userSettingService.clearSettingsFromGroup(groupName);
    }
}
