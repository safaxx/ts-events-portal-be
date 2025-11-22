package com.techsisters.gatherly.entity;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "techsister_user", indexes = {
        @Index(name = "idx_email", columnList = "email")
})
public class User extends AbstractAuditablePersistable{

    private String name;
    private int otp;
    private Date otpCreatedDate;
    @Column(unique = true, nullable = false)
    private String email;
    private String country;
    private String role;

}
