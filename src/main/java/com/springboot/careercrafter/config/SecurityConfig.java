package com.springboot.careercrafter.config;

import com.springboot.careercrafter.service.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final MyUserDetailsService myUserDetailsService;
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        // onboarding APIs
                        .requestMatchers(HttpMethod.POST, "/api/jobseeker/add").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/employer/add").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/admin/add").permitAll()
                        // login API
                        .requestMatchers(HttpMethod.GET, "/api/auth/login").authenticated()
                        // Admin verifies the employer
                        .requestMatchers(HttpMethod.PUT, "/api/employer/verify/**").hasAuthority("ADMIN")
                        // Job seeker apply / resume / save job
                        .requestMatchers(HttpMethod.POST, "/api/jobApplication/add/**").hasAuthority("JOB_SEEKER")
                        // stats display to ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/stats/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/resume/add/**").hasAuthority("JOB_SEEKER")
                        // Employer posts the job
                        .requestMatchers(HttpMethod.POST, "/api/job/add/**").hasAuthority("EMPLOYER")
                        .requestMatchers(HttpMethod.PUT, "/api/job/**").hasAuthority("EMPLOYER")
                        .requestMatchers(HttpMethod.DELETE, "/api/job/**").hasAuthority("EMPLOYER")
                        .requestMatchers(HttpMethod.POST, "/api/applicationStatusHistory/add/**").hasAuthority("EMPLOYER")
                        .anyRequest().permitAll()
                );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public AuthenticationProvider getProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(myUserDetailsService);
        authenticationProvider.setPasswordEncoder(getEncoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}
