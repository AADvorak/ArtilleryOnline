package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.dto.request.LoginRequest;
import com.github.aadvorak.artilleryonline.dto.request.RegisterRequest;
import com.github.aadvorak.artilleryonline.dto.response.UserResponse;
import com.github.aadvorak.artilleryonline.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserEndpoint {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getCurrentUser() {
        return userService.getCurrentUser();
    }

    @PostMapping
    public UserResponse register(@RequestBody @Valid RegisterRequest request, HttpServletResponse response) {
        var responseWithToken = userService.register(request);
        setCookieWithTokenToResponse(responseWithToken.getToken(), response);
        return responseWithToken.getResponse();
    }

    @PostMapping("/login")
    public UserResponse login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
        var responseWithToken = userService.login(request);
        setCookieWithTokenToResponse(responseWithToken.getToken(), response);
        return responseWithToken.getResponse();
    }

    @DeleteMapping("/logout")
    public void logout(HttpServletResponse response) {
        setCookieWithTokenToResponse(null, response);
    }

    private void setCookieWithTokenToResponse(String token, HttpServletResponse response) {
        var cookie = new Cookie("Token", token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}
