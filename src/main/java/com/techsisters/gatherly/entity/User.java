package com.techsisters.gatherly.entity;

import java.util.Date;

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
public class User extends AbstractAuditablePersistable {

    private String name;
    private int otp; // 6 digit code for verification
    private Date otpCreatedDate;

    @Column(unique = true, nullable = false)
    private String email;
    private String country;
    private String accessToken;
    private Date accessTokenCreatedDate;

}
