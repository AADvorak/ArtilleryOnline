package com.github.aadvorak.artilleryonline.security;

import com.github.aadvorak.artilleryonline.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@Accessors(chain = true)
public class ArtilleryOnlineUserDetails implements UserDetails {

    private final User user;
    private final boolean accountNonExpired = true;
    private final boolean accountNonLocked = true;
    private final boolean credentialsNonExpired = true;
    private final boolean enabled = true;

    public ArtilleryOnlineUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<SimpleGrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }
}
