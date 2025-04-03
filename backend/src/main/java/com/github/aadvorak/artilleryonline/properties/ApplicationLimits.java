package com.github.aadvorak.artilleryonline.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "application.limits")
@Component
@Getter
@Setter
public class ApplicationLimits {

    private int maxRoomMembers;

    private int maxBattles;
}
