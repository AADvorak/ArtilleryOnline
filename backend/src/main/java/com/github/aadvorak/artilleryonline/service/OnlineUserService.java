package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.dto.response.ShortUserResponse;
import com.github.aadvorak.artilleryonline.security.ArtilleryOnlineUserDetails;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OnlineUserService {

    @Autowired
    private SimpUserRegistry registry;

    private final ModelMapper mapper = new ModelMapper();

    public List<ShortUserResponse> getOnlineUsers() {
        return registry.getUsers().stream()
                .map(SimpUser::getPrincipal)
                .filter(principal -> principal instanceof UsernamePasswordAuthenticationToken)
                .map(principal -> ((UsernamePasswordAuthenticationToken) principal).getPrincipal())
                .filter(principal -> principal instanceof ArtilleryOnlineUserDetails)
                .map(principal -> ((ArtilleryOnlineUserDetails) principal).getUser())
                .map(user -> mapper.map(user, ShortUserResponse.class))
                .toList();
    }
}
