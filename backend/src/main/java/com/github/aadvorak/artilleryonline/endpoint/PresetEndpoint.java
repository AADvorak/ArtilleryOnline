package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.battle.preset.VehicleSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.specs.VehicleSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/presets")
@RequiredArgsConstructor
public class PresetEndpoint {

    @GetMapping("/vehicles")
    public Map<String, VehicleSpecs> getVehiclePresets() {
        return Arrays.stream(VehicleSpecsPreset.values())
                .collect(Collectors.toMap(VehicleSpecsPreset::getName, VehicleSpecsPreset::getSpecs));
    }
}
