package com.grepp.nbe1_2_team09.admin.config;

import com.grepp.nbe1_2_team09.admin.jwt.JwtFilter;
import com.grepp.nbe1_2_team09.admin.jwt.JwtUtil;
import com.grepp.nbe1_2_team09.admin.service.CustomUserDetailsService;
import com.grepp.nbe1_2_team09.admin.service.oauth2.OAuth2LoginFailureHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    private static final String[] AUTH_WHITELIST = {
            "/swagger-ui/**", "/api-docs", "/user/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // CSRF, CORS
                .csrf(AbstractHttpConfigurer::disable)
                .cors((Customizer.withDefaults()))

                // 세션 비활성화 (JWT 사용)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // FormLogin, BasicHttp 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                .oauth2Login(oauth2 -> oauth2
                        .failureHandler(oAuth2LoginFailureHandler)
                )

                // JwtAuthFilter를 UsernamePasswordAuthenticationFilter 앞에 추가
                .addFilterBefore(new JwtFilter(customUserDetailsService, jwtUtil), UsernamePasswordAuthenticationFilter.class)

                // 권한 규칙 작성
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers(AUTH_WHITELIST).permitAll()
                                .anyRequest().permitAll()
//                                .anyRequest().authenticated()
                );

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
