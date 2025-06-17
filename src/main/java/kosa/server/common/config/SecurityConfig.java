package kosa.server.common.config;

import kosa.server.common.security.filter.JwtAuthenticationFilter;
import kosa.server.common.security.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint; // <-- 추가
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {

        http
                .cors((cors) -> cors
                        .configurationSource(corsConfigurationSource()));
        // csrf disable
        http
                .csrf((auth) -> auth.disable());
        // Form 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        // http basic 인증 방식 disable;
        http
                .httpBasic((auth) -> auth.disable());
        // customAuthenticationEntryPoint 추가
        http
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                );
        // 인증 엔드포인트 설정
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/api/auth/login", "/api/auth/join", "/api/auth/token/reissue",  "/api/auth/verify", "/api/auth/send-verification-email").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated());
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
