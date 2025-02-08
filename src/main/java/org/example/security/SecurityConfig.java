package org.example.security;

import org.example.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        return http
                .csrf().disable()
                .cors().and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register", "/api/auth/**", "/login/oauth2/code/**", "/oauth2/**", "books/public/**", "/public/**", "/css/**", "/images/default_cover.png").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler()) // Obsługa błędu 403
                .authenticationEntryPoint(authenticationEntryPoint()) // Obsługa błędu 401
                .and()
                .formLogin(form -> form
                        .loginPage("/login") // Strona logowania
                        .loginProcessingUrl("/api/auth/login") // Endpoint do przetwarzania logowania
                        .defaultSuccessUrl("/", true) // Przekierowanie po udanym logowaniu
                        .failureUrl("/login?error=true") // Przekierowanie po nieudanym logowaniu
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login") // Strona logowania (może być ta sama co dla formularza)
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.sendRedirect("/login?error=403");
        };
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.sendRedirect("/login");
        };
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http, PasswordEncoder passwordEncoder, UserDetailsService userDetailsService)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> User.builder()
                .username(username)
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
