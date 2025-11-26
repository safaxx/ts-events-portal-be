package com.techsisters.gatherly.filter;

import java.io.IOException;

import com.techsisters.gatherly.service.CustomUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.techsisters.gatherly.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Get the request URI to check if it's a public endpoint
        String requestURI = request.getRequestURI();
        boolean isPublicEndpoint = requestURI.contains("/public/") || requestURI.contains("/auth/");

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Check for the Authorization header and "Bearer " prefix
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // If it's a public endpoint and no token is provided, continue without authentication
            if (isPublicEndpoint) {
                log.debug("Public endpoint accessed without authentication: {}", requestURI);
                filterChain.doFilter(request, response);
                return;
            }
            // For protected endpoints, continue (Spring Security will handle unauthorized access)
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the token
        jwt = authHeader.substring(7); // "Bearer ".length()

        try {
            // 3. Extract the username from the token
            username = jwtService.extractUsername(jwt);

            // 4. Check if user is not already authenticated
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 5. Load user details from the database
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // 6. Validate the token
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // 7. Create an authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // We don't have credentials
                            userDetails.getAuthorities());

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    // 8. Update the SecurityContextHolder
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // If token is invalid or expired, log it but don't fail the request for public endpoints
            if (isPublicEndpoint) {
                log.warn("Invalid JWT token on public endpoint: {} - {}", requestURI, e.getMessage());
                // Continue without authentication for public endpoints
                filterChain.doFilter(request, response);
                return;
            }
            // For protected endpoints, let the exception propagate
            log.error("JWT authentication failed: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT token");
            return;
        }

        // 9. Continue the filter chain
        filterChain.doFilter(request, response);
    }

}
