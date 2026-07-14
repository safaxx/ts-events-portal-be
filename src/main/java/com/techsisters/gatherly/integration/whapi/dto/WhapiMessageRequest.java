package com.techsisters.gatherly.integration.whapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class WhapiMessageRequest {

    private String to;
    private String body;

}
