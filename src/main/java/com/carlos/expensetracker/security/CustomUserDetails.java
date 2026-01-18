package com.carlos.expensetracker.security;

import com.carlos.expensetracker.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

//TODO: getPassword is wrong! Preencher getUsername(), getPassword() - Mapear UserRole para GrantedAuthority
@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; //true by now
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; //true by now
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; //true by now
    }

    @Override
    public boolean isEnabled() {
        return true; //true by now
    }

    //utility
    public UUID getUserId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getUsernameField() {
        return user.getUsername();
    }
}
