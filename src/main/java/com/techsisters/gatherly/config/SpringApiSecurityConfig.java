package com.techsisters.gatherly.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.techsisters.gatherly.filter.JwtAuthenticationFilter;
import com.techsisters.gatherly.service.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SpringApiSecurityConfig {

    /**
     * Injects the comma-separated list of allowed CORS origins from
     * the application properties file (e.g., application.properties).
     * The default value is provided using the colon (:) operator.
     */
    @Value("${app.cors.allowed-origins:http://localhost:3000}")
    private String allowedOriginsString;

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    // ============================================
    // SECURITY FILTER CHAIN
    // ============================================

    /**
     * Main security configuration.
     * Defines which endpoints are public vs protected, and configures JWT
     * filtering.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (Cross-Site Request Forgery)
                // We disable this because we are using stateless REST APIs (not HTML forms).
                // Our React app will send a JWT, not a session cookie.
                .csrf(csrf -> csrf.disable())
                // This line tells Spring Security to use the 'corsConfigurationSource' bean
                // defined below.
                .cors(Customizer.withDefaults())
                .authenticationProvider(authenticationProvider())
                // Configure Session Management to be STATELESS
                // This is essential for REST APIs.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Set up authorization rules
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Whitelist your "send-otp" and other auth endpoints
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/oauth2callback/**").permitAll()
                        .requestMatchers("/public/events/**").permitAll()
                        // Whitelist static resources
                        .requestMatchers("/", "/index.html", "/static/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptions -> exceptions.accessDeniedHandler(new ApiAccessDeniedHandler())
                        .authenticationEntryPoint(new ApiAuthenticationEntryPoint()));
        return http.build();
    }

    // ============================================
    // CORS CONFIGURATION
    // ============================================
    // This bean configures what origins, methods, and headers are allowed.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Convert the injected string into a List<String>
        // The trim() and filter(s -> !s.isEmpty()) ensure no empty strings are left if
        // the property is empty or has extra commas.
        List<String> allowedOrigins = Arrays.stream(allowedOriginsString.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        // 2. Set the origins from the application property
        configuration.setAllowedOrigins(allowedOrigins);

        // Allow all necessary HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow all headers
        configuration.setAllowedHeaders(List.of("*"));

        // Allow credentials (if you use cookies)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply this configuration to all routes
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // ============================================
    // AUTHENTICATION BEANS
    // ============================================

    /**
     * Configures how users are authenticated.
     * Uses DaoAuthenticationProvider to load users from database via
     * UserDetailsService.
     */

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(passwordEncoder());
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }

    ;

    /**
     * Password encoder bean.
     * Required by Spring Security even though we use OTP authentication.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication manager bean.
     * Can be used for manual authentication if needed.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
