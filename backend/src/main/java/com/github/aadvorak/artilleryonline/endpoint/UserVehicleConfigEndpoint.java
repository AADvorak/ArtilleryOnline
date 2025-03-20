package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.dto.UserVehicleConfigDto;
import com.github.aadvorak.artilleryonline.service.UserVehicleConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-vehicle-configs/{vehicleName}")
@RequiredArgsConstructor
public class UserVehicleConfigEndpoint {

    private final UserVehicleConfigService userVehicleConfigService;

    @GetMapping
    public UserVehicleConfigDto load(@PathVariable(name = "vehicleName") String vehicleName) {
        return userVehicleConfigService.load(vehicleName);
    }

    @PostMapping
    public void save(
            @PathVariable(name = "vehicleName") String vehicleName,
            @RequestBody UserVehicleConfigDto userVehicleConfigDto
    ) {
        userVehicleConfigService.save(vehicleName, userVehicleConfigDto);
    }
}
