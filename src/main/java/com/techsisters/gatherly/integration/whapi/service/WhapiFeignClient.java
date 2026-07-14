package com.techsisters.gatherly.integration.whapi.service;

import com.techsisters.gatherly.integration.whapi.dto.WhapiMessageRequest;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.Response;

public interface WhapiFeignClient {

    @RequestLine("POST /messages/text")
    @Headers({
            "Authorization: {authHeader}",
            "Content-Type: application/json"
    })
    Response sendTextMessage(@Param("authHeader") String authHeader, WhapiMessageRequest request);
}
