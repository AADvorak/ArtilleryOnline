package com.github.aadvorak.artilleryonline.endpoint;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfEndpoint {

    @RequestMapping("/api/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }
}
