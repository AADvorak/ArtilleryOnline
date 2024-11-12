package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.dto.request.UserSettingRequest;
import com.github.aadvorak.artilleryonline.dto.response.UserSettingResponse;
import com.github.aadvorak.artilleryonline.entity.UserSetting;
import com.github.aadvorak.artilleryonline.error.exception.NotFoundAppException;
import com.github.aadvorak.artilleryonline.repository.UserSettingRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSettingService {

    private final static List<String> AVAILABLE_GROUP_NAMES = List.of("controls", "appearances", "sounds");

    private final UserSettingRepository userSettingRepository;

    private final UserService userService;

    private final ModelMapper mapper = new ModelMapper();

    public Map<String, List<UserSettingResponse>> getSettings() {
        var user = userService.getUserFromContext();
        var settings = userSettingRepository.findByUserId(user.getId());
        Map<String, List<UserSettingResponse>> settingsMap = new HashMap<>();
        AVAILABLE_GROUP_NAMES.forEach(groupName -> {
            var filteredSettings = settings.stream()
                    .filter(setting -> setting.getGroupName().equals(groupName))
                    .map(setting -> mapper.map(setting, UserSettingResponse.class))
                    .toList();
            settingsMap.put(groupName, filteredSettings);
        });
        return settingsMap;
    }

    public List<UserSettingResponse> getSettingsFromGroup(String groupName) {
        if (!AVAILABLE_GROUP_NAMES.contains(groupName)) {
            throw new NotFoundAppException();
        }
        var user = userService.getUserFromContext();
        return userSettingRepository.findByUserIdAndGroupName(user.getId(), groupName).stream()
                .map(setting -> mapper.map(setting, UserSettingResponse.class))
                .collect(Collectors.toList());
    }

    public void setSetting(String groupName, UserSettingRequest request) {
        if (!AVAILABLE_GROUP_NAMES.contains(groupName)) {
            throw new NotFoundAppException();
        }
        var user = userService.getUserFromContext();
        var existingSetting = userSettingRepository.findByUserIdAndGroupNameAndName(user.getId(),
                groupName, request.getName());
        if (existingSetting.isPresent()) {
            var setting = existingSetting.get();
            setting.setValue(request.getValue());
            userSettingRepository.save(setting);
        } else {
            var setting = new UserSetting();
            setting.setUserId(user.getId());
            setting.setGroupName(groupName);
            setting.setName(request.getName());
            setting.setValue(request.getValue());
            userSettingRepository.save(setting);
        }
    }

    @Transactional
    public void clearSettingsFromGroup(String groupName) {
        if (!AVAILABLE_GROUP_NAMES.contains(groupName)) {
            throw new NotFoundAppException();
        }
        var user = userService.getUserFromContext();
        userSettingRepository.deleteByUserIdAndGroupName(user.getId(), groupName);
    }
}
