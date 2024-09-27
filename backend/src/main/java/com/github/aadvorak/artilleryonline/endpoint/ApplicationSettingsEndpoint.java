package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.properties.ApplicationSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/application/settings")
@RequiredArgsConstructor
public class ApplicationSettingsEndpoint {

    private final ApplicationSettings applicationSettings;

    @GetMapping
    public ApplicationSettings getApplicationSettings() {
        return applicationSettings;
    }
}
