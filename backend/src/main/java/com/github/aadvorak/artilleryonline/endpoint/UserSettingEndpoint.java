package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.dto.request.UserSettingRequest;
import com.github.aadvorak.artilleryonline.dto.response.UserSettingResponse;
import com.github.aadvorak.artilleryonline.service.UserSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-settings")
@RequiredArgsConstructor
public class UserSettingEndpoint {

    private final UserSettingService userSettingService;

    @GetMapping()
    public Map<String, List<UserSettingResponse>> getSettings() {
        return userSettingService.getSettings();
    }

    @GetMapping("/{groupName}")
    public List<UserSettingResponse> getSettingsFromGroup(@PathVariable String groupName) {
        return userSettingService.getSettingsFromGroup(groupName);
    }

    @PutMapping("/{groupName}")
    public void setSetting(@PathVariable String groupName, @RequestBody UserSettingRequest request) {
        userSettingService.setSetting(groupName, request);
    }

    @DeleteMapping("/{groupName}")
    public void clearSettingsFromGroup(@PathVariable String groupName) {
        userSettingService.clearSettingsFromGroup(groupName);
    }

    @DeleteMapping("/{groupName}/{name}")
    public void clearSetting(@PathVariable String groupName, @PathVariable String name) {
        userSettingService.clearSetting(groupName, name);
    }
}
