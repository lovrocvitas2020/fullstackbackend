package com.example.fullstackcrudreact.fullstackbackend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                    "/users", 
                    "/register", 
                    "/login",
                    "/loginuser",
                    // Remove public access to modification endpoints
                    // "/add_user_notes",
                    // "/update_user_note/**",
                    // "/delete_user_note/**",
                    "/user_notes",
                    "/xls",
                    "/viewworklog",
                    "/worklog/**",
                    "/add_worklog",
                    "/update_worklog/**",
                    "/delete_worklog/**",
                    "/user_notes/**",
                    "/userdetails/**",
                    "/adduserdetails/**",
                    "/reset-password/**",
                    "/user/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(formLogin -> formLogin
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .permitAll()
            )
            .logout(logout -> logout
                .permitAll()
                .logoutSuccessUrl("/login?logout")
            )
            .rememberMe(rememberMe -> rememberMe
                .userDetailsService(customUserDetailsService)
            )
            .csrf(csrf -> csrf.disable()); // Consider enabling CSRF with token management

        return http.build();
    }

    // Remove if CustomUserDetailsService is already a Spring bean
    // @Bean
    // public UserDetailsService userDetailsService() {
    //     return customUserDetailsService;
    // }
}