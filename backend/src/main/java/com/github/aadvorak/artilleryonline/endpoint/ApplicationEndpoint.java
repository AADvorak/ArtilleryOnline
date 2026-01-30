package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.properties.ApplicationLimits;
import com.github.aadvorak.artilleryonline.properties.ApplicationSettings;
import com.github.aadvorak.artilleryonline.service.OnlineUserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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

    private final ApplicationLimits applicationLimits;

    private final OnlineUserService onlineUserService;

    private final ThreadPoolTaskExecutor runBattleExecutor;

    @GetMapping("/settings")
    public ApplicationSettings getApplicationSettings() {
        return applicationSettings;
    }

    @GetMapping("/limits")
    public ApplicationLimits getApplicationLimits() {
        return applicationLimits;
    }

    @GetMapping("/timezone")
    public Map<String, Integer> getTimezoneOffset() {
        return Map.of("offset", ZonedDateTime.now().getOffset().getTotalSeconds());
    }

    @GetMapping("/counts")
    public CountsResponse getCounts() {
        return new CountsResponse()
                .setBattles(runBattleExecutor.getActiveCount())
                .setOnlineUsers(onlineUserService.count());
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static final class CountsResponse {
        private long battles;
        private long onlineUsers;
    }
}
