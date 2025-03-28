package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.dto.request.EditUserRequest;
import com.github.aadvorak.artilleryonline.dto.request.LoginRequest;
import com.github.aadvorak.artilleryonline.dto.request.RegisterRequest;
import com.github.aadvorak.artilleryonline.dto.response.ShortUserResponse;
import com.github.aadvorak.artilleryonline.dto.response.UserResponse;
import com.github.aadvorak.artilleryonline.service.OnlineUserService;
import com.github.aadvorak.artilleryonline.service.RoomService;
import com.github.aadvorak.artilleryonline.service.UserBattleQueueService;
import com.github.aadvorak.artilleryonline.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserEndpoint {

    private final UserService userService;

    private final OnlineUserService onlineUserService;

    private final RoomService roomService;

    private final UserBattleQueueService userBattleQueueService;

    @GetMapping("/online")
    public List<ShortUserResponse> getOnlineUsers() {
        return onlineUserService.getOnlineUsersResponse();
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(HttpServletRequest request) {
        var userResponse = userService.getCurrentUser();
        setCookieFromRequestToUserResponse(request, userResponse);
        return userResponse;
    }

    @PutMapping("/me")
    public UserResponse editCurrentUser(@RequestBody @Valid EditUserRequest editUserRequest, HttpServletRequest request) {
        var userResponse = userService.editCurrentUser(editUserRequest);
        setCookieFromRequestToUserResponse(request, userResponse);
        return userResponse;
    }

    @PostMapping
    public UserResponse register(@RequestBody @Valid RegisterRequest request, HttpServletResponse response) {
        var userResponse = userService.register(request);
        setCookieWithTokenToResponse(userResponse.getToken(), response);
        return userResponse;
    }

    @PostMapping("/login")
    public UserResponse login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
        var userResponse = userService.login(request);
        setCookieWithTokenToResponse(userResponse.getToken(), response);
        return userResponse;
    }

    @DeleteMapping("/logout")
    public void logout(HttpServletResponse response) {
        roomService.exitRoom();
        userBattleQueueService.removeUserFromQueue();
        setCookieWithTokenToResponse(null, response);
    }

    private void setCookieWithTokenToResponse(String token, HttpServletResponse response) {
        var cookie = new Cookie("Token", token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    private void setCookieFromRequestToUserResponse(HttpServletRequest request, UserResponse userResponse) {
        Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("Token"))
                .map(Cookie::getValue)
                .findAny()
                .ifPresent(userResponse::setToken);
    }
}
