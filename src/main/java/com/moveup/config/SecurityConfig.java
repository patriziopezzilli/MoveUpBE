package com.moveup.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
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

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/sports/**", "/api/lessons/search", "/api/reviews/instructor/**").permitAll()
                        .requestMatchers("/api/payments/webhook").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        
                        // User endpoints
                        .requestMatchers(HttpMethod.GET, "/api/users/profile").hasAnyRole("USER", "INSTRUCTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAnyRole("USER", "INSTRUCTOR")
                        
                        // Instructor specific endpoints
                        .requestMatchers("/api/instructors/register", "/api/instructors/profile").hasRole("INSTRUCTOR")
                        .requestMatchers(HttpMethod.POST, "/api/lessons/**").hasRole("INSTRUCTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/lessons/**").hasRole("INSTRUCTOR")
                        
                        // Booking endpoints (both users and instructors)
                        .requestMatchers("/api/bookings/**").hasAnyRole("USER", "INSTRUCTOR")
                        
                        // Payment endpoints
                        .requestMatchers("/api/payments/**").hasAnyRole("USER", "INSTRUCTOR")
                        
                        // Reviews (users can create, instructors can respond)
                        .requestMatchers(HttpMethod.POST, "/api/reviews").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/reviews/*/response").hasRole("INSTRUCTOR")
                        
                        // Notifications and badges
                        .requestMatchers("/api/notifications/**", "/api/badges/user/**", "/api/badges/check/**").hasAnyRole("USER", "INSTRUCTOR")
                        
                        // Admin endpoints (if needed)
                        .requestMatchers(HttpMethod.POST, "/api/badges", "/api/sports").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/badges/**", "/api/sports/**").hasRole("ADMIN")
                        
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}