package com.techsisters.gatherly.entity;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "techsister_user", indexes = {
        @Index(name = "idx_email", columnList = "email")
})
public class User extends AbstractAuditablePersistable implements UserDetails {

    private String name;
    private int otp; // 6 digit code for verification
    private Date otpCreatedDate;

    @Column(unique = true, nullable = false)
    private String email;
    private String country;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_API_USER"));
    }

    @Override
    public String getPassword() {
        // Not using passwords, this can be null.
        return null;
    }

    @Override
    public String getUsername() {
        // This is still the unique identifier
        return this.email;
    }

}
