package com.complete.todayspace.global.config;

import com.complete.todayspace.domain.user.repository.UserRepository;
import com.complete.todayspace.global.jwt.JwtProvider;
import com.complete.todayspace.global.security.UserDetailsServiceImpl;
import com.complete.todayspace.global.security.exception.CustomAccessDeniedHandler;
import com.complete.todayspace.global.security.exception.CustomAuthenticationEntryPoint;
import com.complete.todayspace.global.security.filter.JwtAuthenticationFilter;
import com.complete.todayspace.global.security.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final UserDetailsServiceImpl userDetailsService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtProvider, userRepository);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtProvider, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.cors(cors -> {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedOriginPatterns(Collections.singletonList("*"));
            corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
            corsConfiguration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
            corsConfiguration.setExposedHeaders(List.of("Authorization", "X-Custom-Header"));
            corsConfiguration.setAllowCredentials(true);
            cors.configurationSource(request -> corsConfiguration);
        });

        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/v1/auth/signup", "/v1/auth/refresh", "/v1/auth/check").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/v1/products", "/v1/products**", "/v1/products/*",
                                "/v1/posts", "/v1/posts/*", "/v1/hashtags",
                                "/v1/kakao/callback", "/v1/naver/callback", "/v1/google/callback").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exceptionHandling -> {
                    exceptionHandling.authenticationEntryPoint(customAuthenticationEntryPoint);
                    exceptionHandling.accessDeniedHandler(customAccessDeniedHandler);
                })
                .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter(), JwtAuthorizationFilter.class);

        return http.build();
    }
}
