package com.catfood.demo.config;

import com.catfood.demo.auth.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  private final JwtAuthFilter jwtAuthFilter;
  private final JsonAuthHandlers jsonAuthHandlers;

  public SecurityConfig(JwtAuthFilter jwtAuthFilter, JsonAuthHandlers jsonAuthHandlers) {
    this.jwtAuthFilter = jwtAuthFilter;
    this.jsonAuthHandlers = jsonAuthHandlers;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return username -> {
      throw new UsernameNotFoundException("Use JWT login");
    };
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/users/register")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/cans", "/api/cans/**")
                    .permitAll()
                    .requestMatchers("/api/admin/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
                    .permitAll()
                    .requestMatchers("/api/**")
                    .authenticated()
                    .anyRequest()
                    .permitAll())
        .exceptionHandling(
            exceptions ->
                exceptions
                    .authenticationEntryPoint(jsonAuthHandlers)
                    .accessDeniedHandler(jsonAuthHandlers))
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
