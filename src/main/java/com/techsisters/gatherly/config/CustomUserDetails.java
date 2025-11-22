package com.techsisters.gatherly.config;

import com.techsisters.gatherly.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Wrapper class that adapts our User entity to Spring Security's UserDetails interface.
 * This separates security concerns from the domain model.
 */
@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getRole() == null || user.getRole().isEmpty()) {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
        if ("ADMIN".equals(user.getRole())) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER")
            );
        }

        // All other roles get single authority
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }


    @Override
    public String getPassword() {
        // Not using passwords - OTP authentication
        return null;
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Expose the underlying User entity for easy access
    public User getUser() {
        return user;
    }
}
