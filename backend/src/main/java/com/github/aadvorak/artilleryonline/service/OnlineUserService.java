package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.dto.response.ShortUserResponse;
import com.github.aadvorak.artilleryonline.entity.User;
import com.github.aadvorak.artilleryonline.security.ArtilleryOnlineUserDetails;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class OnlineUserService {

    private final SimpUserRegistry registry;

    private final ModelMapper mapper = new ModelMapper();

    public List<ShortUserResponse> getOnlineUsersResponse() {
        return getOnlineUsers()
                .map(user -> mapper.map(user, ShortUserResponse.class))
                .toList();
    }

    public Optional<User> findByNickname(String nickname) {
        return getOnlineUsers()
                .filter(user -> user.getNickname().equals(nickname))
                .findAny();
    }

    public long count() {
        return registry.getUsers().size();
    }

    private Stream<User> getOnlineUsers() {
        return registry.getUsers().stream()
                .map(SimpUser::getPrincipal)
                .filter(principal -> principal instanceof UsernamePasswordAuthenticationToken)
                .map(principal -> ((UsernamePasswordAuthenticationToken) principal).getPrincipal())
                .filter(principal -> principal instanceof ArtilleryOnlineUserDetails)
                .map(principal -> ((ArtilleryOnlineUserDetails) principal).getUser());
    }
}
