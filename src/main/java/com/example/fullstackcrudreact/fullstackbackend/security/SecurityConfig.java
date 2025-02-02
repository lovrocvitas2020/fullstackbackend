package com.example.fullstackcrudreact.fullstackbackend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors()
            .and()
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                    "/users", 
                    "/register", 
                    "/loginuser",
                    "/add_user_notes",
                    "/batch/start",
                    "/update_user_note/**",
                    "/delete_user_note/**",
                    "/user_notes",
                    "/xls",
                    "/viewworklog",
                    "/worklog/**",
                    "/add_worklog",
                    "/update_worklog/**",
                    "/delete_worklog/**",
                    "/user_notes/**",
                    "/user/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(formLogin -> formLogin
                .loginPage("/login")
                .defaultSuccessUrl("/home",true)
                .permitAll()
            )
            .logout(logout -> logout.permitAll())
            .rememberMe(Customizer.withDefaults())
            .csrf().disable();

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(); // Replace this with your custom implementation if needed
    }
}
