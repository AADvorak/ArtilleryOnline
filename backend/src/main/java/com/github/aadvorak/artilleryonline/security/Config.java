package com.github.aadvorak.artilleryonline.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class Config {

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, UserDetailsService jwtUserDetailsService)
            throws Exception {
        var manager = http.getSharedObject(AuthenticationManagerBuilder.class);

        manager
                .userDetailsService(jwtUserDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder());

        return manager.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/users/**").permitAll()
                .requestMatchers("/api/battles/**").permitAll()
                .requestMatchers("/api/application/**").permitAll()
                .anyRequest().permitAll());

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
