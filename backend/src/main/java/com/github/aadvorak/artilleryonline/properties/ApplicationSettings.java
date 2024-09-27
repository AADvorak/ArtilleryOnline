package com.github.aadvorak.artilleryonline.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "application.settings")
@Component
@Getter
@Setter
public class ApplicationSettings {

    private boolean debug;

    private boolean clientProcessing;
}
