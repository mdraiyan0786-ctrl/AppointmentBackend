package com.appointment.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                // CORS
                .cors(Customizer.withDefaults())

                // Disable CSRF because we are using JWT
                .csrf(csrf -> csrf.disable())

                // JWT = stateless
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // Authorization
                .authorizeHttpRequests(auth -> auth

                        // CORS preflight
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        // User Login / Register
                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()

                        // Doctor Registration / Login
                        // Doctor Registration / Login / Password Reset
                        .requestMatchers(
                                "/api/doctors/register",
                                "/api/doctors/login",
                                "/api/doctors/reset-password"
                        ).permitAll()

                        // Doctors - public GET
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/doctors"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/doctors/**"
                        ).permitAll()

                        // Doctor availability
                        .requestMatchers(
                                "/api/availability/**"
                        ).permitAll()

                        // User registration
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/users"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/doctors/login",
                                "/api/doctors/register"
                        ).permitAll()

                        // Everything else requires JWT
                        .anyRequest().authenticated()
                );

        // JWT filter
        http.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }


    // =========================
    // CORS CONFIGURATION
    // =========================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type"
                )
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }


    // =========================
    // AUTHENTICATION MANAGER
    // =========================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }
}