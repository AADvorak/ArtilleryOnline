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
    public List<UserSettingResponse> getSettingsFromGroup(@PathVariable(name = "groupName") String groupName) {
        return userSettingService.getSettingsFromGroup(groupName);
    }

    @PutMapping("/{groupName}")
    public void setSetting(@PathVariable(name = "groupName") String groupName, @RequestBody UserSettingRequest request) {
        userSettingService.setSetting(groupName, request);
    }

    @DeleteMapping("/{groupName}")
    public void clearSettingsFromGroup(@PathVariable(name = "groupName") String groupName) {
        userSettingService.clearSettingsFromGroup(groupName);
    }
}
