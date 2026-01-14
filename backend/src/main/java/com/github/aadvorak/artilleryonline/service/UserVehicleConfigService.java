package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.battle.config.AmmoConfig;
import com.github.aadvorak.artilleryonline.battle.preset.VehicleSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import com.github.aadvorak.artilleryonline.dto.UserVehicleConfigDto;
import com.github.aadvorak.artilleryonline.entity.UserVehicleConfig;
import com.github.aadvorak.artilleryonline.error.exception.BadRequestAppException;
import com.github.aadvorak.artilleryonline.error.response.Validation;
import com.github.aadvorak.artilleryonline.error.response.ValidationResponse;
import com.github.aadvorak.artilleryonline.repository.UserVehicleConfigRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserVehicleConfigService {

    private final UserService userService;

    private final UserVehicleConfigRepository userVehicleConfigRepository;

    public UserVehicleConfigDto load(String vehicleName) {
        var user = userService.getUserFromContext();
        return load(vehicleName, user.getId());
    }

    public UserVehicleConfigDto load(String vehicleName, long userId) {
        var specs = getVehicleSpecs(vehicleName);
        var configs = userVehicleConfigRepository.findByUserIdAndVehicleName(userId, vehicleName);
        var dto = createDto(configs);
        try {
            validateDto(dto, specs);
            return dto;
        } catch (BadRequestAppException e) {
            userVehicleConfigRepository.deleteAll(configs);
            return new UserVehicleConfigDto();
        }
    }

    public void save(String vehicleName, UserVehicleConfigDto dto) {
        var specs = getVehicleSpecs(vehicleName);
        validateDto(dto, specs);
        var user = userService.getUserFromContext();
        var configs = userVehicleConfigRepository.findByUserIdAndVehicleName(user.getId(), vehicleName);
        saveGunConfig(configs, user.getId(), vehicleName, dto);
        for (int priority = 0; priority < dto.getAmmo().size();  priority++) {
            saveAmmoConfig(configs, user.getId(), vehicleName, dto.getAmmo().get(priority), priority);
        }
        var ammoNames = dto.getAmmo().stream().map(AmmoConfig::getName).toList();
        configs.stream()
                .filter(config -> ConfigName.AMMO.getName().equals(config.getName()))
                .filter(config -> !ammoNames.contains(config.getValue()))
                .forEach(userVehicleConfigRepository::delete);
    }

    private void saveGunConfig(List<UserVehicleConfig> configs, long userId, String vehicleName, UserVehicleConfigDto dto) {
        var gunConfig = configs.stream()
                .filter(config -> ConfigName.GUN.getName().equals(config.getName()))
                .findAny().orElse(null);
        if (gunConfig == null) {
            gunConfig = new UserVehicleConfig();
            gunConfig.setUserId(userId);
            gunConfig.setVehicleName(vehicleName);
            gunConfig.setName(ConfigName.GUN.getName());
        }
        gunConfig.setValue(dto.getGun());
        userVehicleConfigRepository.save(gunConfig);
    }

    private void saveAmmoConfig(List<UserVehicleConfig> configs, long userId, String vehicleName, AmmoConfig entry, int priority) {
        var ammoConfig = configs.stream()
                .filter(config -> ConfigName.AMMO.getName().equals(config.getName()))
                .filter(config -> config.getValue().equals(entry.getName()))
                .findAny().orElse(null);
        if (ammoConfig == null) {
            ammoConfig = new UserVehicleConfig();
            ammoConfig.setUserId(userId);
            ammoConfig.setVehicleName(vehicleName);
            ammoConfig.setName(ConfigName.AMMO.getName());
            ammoConfig.setValue(entry.getName());
        }
        ammoConfig.setAmount(entry.getAmount());
        ammoConfig.setPriority(priority);
        userVehicleConfigRepository.save(ammoConfig);
    }

    private VehicleSpecs getVehicleSpecs(String vehicleName) {
        final var field = "vehicleName";
        if (vehicleName == null || vehicleName.isEmpty()) {
            throw new BadRequestAppException(List.of(new ValidationResponse()
                    .setValidation(Validation.EMPTY)
                    .setField(field)));
        }
        return Arrays.stream(VehicleSpecsPreset.values())
                .filter(vehicleSpecsPreset -> vehicleSpecsPreset.getName().equals(vehicleName))
                .map(VehicleSpecsPreset::getSpecs)
                .findAny()
                .orElseThrow(() -> new BadRequestAppException(List.of(new ValidationResponse()
                        .setValidation(Validation.EMPTY)
                        .setField(field))));
    }

    private UserVehicleConfigDto createDto(List<UserVehicleConfig> configs) {
        var gun = configs.stream()
                .filter(config -> ConfigName.GUN.getName().equals(config.getName()))
                .map(UserVehicleConfig::getValue)
                .findAny().orElse(null);
        var ammo = configs.stream()
                .filter(config -> ConfigName.AMMO.getName().equals(config.getName()))
                .sorted(Comparator
                        .comparingInt(UserVehicleConfig::getPriorityOrDefault)
                        .thenComparing(UserVehicleConfig::getValue))
                .map(userVehicleConfig -> new AmmoConfig()
                        .setName(userVehicleConfig.getValue())
                        .setAmount(userVehicleConfig.getAmount()))
                .toList();
        return new UserVehicleConfigDto()
                .setGun(gun)
                .setAmmo(ammo);
    }

    private void validateDto(UserVehicleConfigDto dto, VehicleSpecs vehicleSpecs) {
        var errors = new ArrayList<ValidationResponse>();
        if (dto.getGun() == null || dto.getGun().isEmpty()) {
            errors.add(new ValidationResponse()
                    .setValidation(Validation.EMPTY)
                    .setField(ConfigName.GUN.getName()));
        }
        if (dto.getAmmo() == null || dto.getAmmo().isEmpty()) {
            errors.add(new ValidationResponse()
                    .setValidation(Validation.EMPTY)
                    .setField(ConfigName.GUN.getName()));
        }
        if (!errors.isEmpty()) {
            throw new BadRequestAppException(errors);
        }
        var gunSpecs = vehicleSpecs.getAvailableGuns().get(dto.getGun());
        if (gunSpecs == null) {
            errors.add(new ValidationResponse()
                    .setValidation(Validation.WRONG)
                    .setField(ConfigName.GUN.getName()));
            throw new BadRequestAppException(errors);
        }
        var availableShellNames = gunSpecs.getAvailableShells().keySet();
        dto.getAmmo().stream().map(AmmoConfig::getName).forEach(shellName -> {
            if (!availableShellNames.contains(shellName)) {
                errors.add(new ValidationResponse()
                        .setValidation(Validation.WRONG)
                        .setField(ConfigName.AMMO.getName()));
                throw new BadRequestAppException(errors);
            }
        });
        var sumAmount = dto.getAmmo().stream().map(AmmoConfig::getAmount).reduce(0, Integer::sum);
        if (sumAmount > gunSpecs.getAmmo()) {
            errors.add(new ValidationResponse()
                    .setValidation(Validation.WRONG)
                    .setField(ConfigName.AMMO.getName()));
            throw new BadRequestAppException(errors);
        }
    }

    @Getter
    @RequiredArgsConstructor
    private enum ConfigName {
        GUN("gun"),
        AMMO("ammo");

        private final String name;
    }
}
