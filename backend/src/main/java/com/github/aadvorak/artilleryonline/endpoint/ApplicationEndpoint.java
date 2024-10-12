package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.properties.ApplicationSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/application")
@RequiredArgsConstructor
public class ApplicationEndpoint {

    private final ApplicationSettings applicationSettings;

    @GetMapping("/settings")
    public ApplicationSettings getApplicationSettings() {
        return applicationSettings;
    }

    @GetMapping(path = "/timezone")
    public Map<String, Integer> getTimezoneOffset() {
        return Map.of("offset", ZonedDateTime.now().getOffset().getTotalSeconds());
    }
}
