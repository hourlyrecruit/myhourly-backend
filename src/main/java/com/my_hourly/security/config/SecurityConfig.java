package com.my_hourly.security.config;

import com.my_hourly.security.handler.JwtAccessDeniedHandler;
import com.my_hourly.security.handler.JwtAuthenticationEntryPoint;
import com.my_hourly.security.token.JwtAuthenticationFilter;
import com.my_hourly.security.user.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    private final JwtAccessDeniedHandler accessDeniedHandler;

    private final CustomUserDetailsService customUserDetailsService;

    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                .csrf(csrf -> csrf.disable())

                .cors(Customizer.withDefaults())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(SecurityConstants.PUBLIC_ENDPOINTS).permitAll()

                        .anyRequest()

                        .authenticated()

                )

                .authenticationProvider(authenticationProvider())

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();

    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(customUserDetailsService);

        provider.setPasswordEncoder(
                passwordEncoder
        );

        return provider;

    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();

    }

}
