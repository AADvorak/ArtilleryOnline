package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.dto.request.EditUserRequest;
import com.github.aadvorak.artilleryonline.dto.request.LoginRequest;
import com.github.aadvorak.artilleryonline.dto.request.RegisterRequest;
import com.github.aadvorak.artilleryonline.dto.response.UserResponse;
import com.github.aadvorak.artilleryonline.entity.User;
import com.github.aadvorak.artilleryonline.error.exception.AuthenticationAppException;
import com.github.aadvorak.artilleryonline.error.exception.BadRequestAppException;
import com.github.aadvorak.artilleryonline.error.response.Validation;
import com.github.aadvorak.artilleryonline.error.response.ValidationResponse;
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
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final List<ValidationResponse> BAD_CREDENTIALS_VALIDATION = List.of(
            new ValidationResponse()
                    .setValidation(Validation.WRONG)
                    .setField("email"),
            new ValidationResponse()
                    .setValidation(Validation.WRONG)
                    .setField("password")
    );

    private static final List<String> UNIQUE_USER_FIELDS = List.of("email", "nickname");

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;

    private final ModelMapper mapper = new ModelMapper();

    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public UserResponse login(LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            if (auth.isAuthenticated()) {
                User user = ((ArtilleryOnlineUserDetails) auth.getPrincipal()).getUser();
                return createUserResponseWithToken(user);
            } else {
                throw new BadRequestAppException(BAD_CREDENTIALS_VALIDATION);
            }
        } catch (BadCredentialsException e) {
            throw new BadRequestAppException(BAD_CREDENTIALS_VALIDATION);
        }
    }

    public UserResponse register(RegisterRequest request) {
        User user = mapper.map(request, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            user = userRepository.save(user);
            return createUserResponseWithToken(user);
        } catch (DataIntegrityViolationException ex) {
            throw createUniqueFieldException(ex);
        }
    }

    public UserResponse getCurrentUser() {
        User user = getUserFromContext();
        return mapper.map(user, UserResponse.class);
    }

    public UserResponse editCurrentUser(EditUserRequest request) {
        User user = getUserFromContext();
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        try {
            user = userRepository.save(user);
            return mapper.map(user, UserResponse.class);
        } catch (DataIntegrityViolationException ex) {
            throw createUniqueFieldException(ex);
        }
    }

    public User getUserFromContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof UsernamePasswordAuthenticationToken) {
            return ((ArtilleryOnlineUserDetails) auth.getPrincipal()).getUser();
        } else {
            throw new AuthenticationAppException();
        }
    }

    private UserResponse createUserResponseWithToken(User user) {
        UserResponse response = mapper.map(user, UserResponse.class);
        response.setToken(jwtTokenUtil.generateToken(user.getEmail()));
        return response;
    }

    private BadRequestAppException createUniqueFieldException(DataIntegrityViolationException ex) {
        return new BadRequestAppException(UNIQUE_USER_FIELDS.stream()
                .filter(field -> ex.getMessage().contains("(" + field + ")"))
                .map(field -> new ValidationResponse()
                        .setValidation(Validation.EXISTS)
                        .setField(field))
                .toList());
    }
}
