package com.example.fullstackcrudreact.fullstackbackend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

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
                    "/send-reset-request/**",
                    "/confirm-reset-password/**",
                    "/addprojects",
                    "/viewprojects",
                    "/editproject/**",
                    "/projects/*",
                    "/viewtasks",
                    "/viewpaymentslips",
                    "/addpaymentslips",
                    "/viewpaymentslip/**",
                    "/deletepaymentslip/**",
                    "/editpaymentslip/**",
                    "/addtask",
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
            .csrf(csrf -> csrf.disable()); 

        return http.build();
    }

     @Bean
    public HttpFirewall defaultHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true); // Allow semicolons in the URL
        return firewall;
    }
}