package com.techsisters.gatherly.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OtpRequest {

    @NotBlank
    private String email;
}
