package com.techsisters.gatherly.config;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.techsisters.gatherly.dto.ResponseDTO;
import com.techsisters.gatherly.util.CommonUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // 401: This is for users who are not authenticated (e.g., they provided no
    // JWT or an invalid/expired JWT) and are trying to access a protected resource

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        log.info("Unauthorized access attempted: {}", authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ResponseDTO jsonResponse = new ResponseDTO();
        jsonResponse.setSuccess(false);
        jsonResponse.setMessage("Error: Unauthorized, please login again");

        // Write the JSON response to the response body
        response.getWriter().write(CommonUtil.convertToJsonString(jsonResponse));

    }

}
