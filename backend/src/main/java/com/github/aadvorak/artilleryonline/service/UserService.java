package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.dto.request.LoginRequest;
import com.github.aadvorak.artilleryonline.dto.request.RegisterRequest;
import com.github.aadvorak.artilleryonline.dto.response.ResponseWithToken;
import com.github.aadvorak.artilleryonline.dto.response.UserResponse;
import com.github.aadvorak.artilleryonline.entity.User;
import com.github.aadvorak.artilleryonline.repository.UserRepository;
import com.github.aadvorak.artilleryonline.security.ArtilleryOnlineUserDetails;
import com.github.aadvorak.artilleryonline.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;

    private final ModelMapper mapper = new ModelMapper();

    public ResponseWithToken<UserResponse> login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        if (auth.isAuthenticated()) {
            User user = ((ArtilleryOnlineUserDetails) auth.getPrincipal()).getUser();
            return createResponseWithToken(user);
        } else {
            throw new BadCredentialsException(request.getEmail());
        }
    }

    public ResponseWithToken<UserResponse> register(RegisterRequest request) {
        User user = mapper.map(request, User.class);
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        try {
            user = userRepository.save(user);
            return createResponseWithToken(user);
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException();
        }
    }

    public UserResponse getCurrentUser() {
        return mapper.map(userRepository.findByEmail(SecurityContextHolder.getContext()
                .getAuthentication().getName()).orElseThrow(), UserResponse.class);
    }

    private ResponseWithToken<UserResponse> createResponseWithToken(User user) {
        UserResponse response = mapper.map(user, UserResponse.class);
        return new ResponseWithToken<UserResponse>()
                .setResponse(response)
                .setToken(jwtTokenUtil.generateToken(user.getEmail()));
    }
}
