package com.blog.myblogs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @SuppressWarnings("deprecation")
    @Bean
    public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/post/published").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/post/published/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/post/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/post/category/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/category").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/comment/post/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        // Admin only endpoints
                        .requestMatchers(HttpMethod.POST, "/api/post").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/post/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/post/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/post/publish/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/post/unpublish/**").hasRole("ADMIN")
                        .requestMatchers("/api/post/unpublished").hasRole("ADMIN")
                        .requestMatchers("/api/post/count/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/category").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/category/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/category/**").hasRole("ADMIN")
                        .requestMatchers("/api/user/admin/**").hasRole("ADMIN")

                        // Author and Admin endpoints
                        .requestMatchers(HttpMethod.GET, "/api/post").hasAnyRole("ADMIN", "AUTHOR")
                        .requestMatchers(HttpMethod.GET, "/api/post/admin/**").hasAnyRole("ADMIN", "AUTHOR")

                        // All authenticated users
                        .requestMatchers(HttpMethod.POST, "/api/comment").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/comment/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/comment/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/post/like/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/post/dislike/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/post/undo-like/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/post/undo-dislike/**").authenticated()
                        .requestMatchers("/api/user/profile/**").authenticated()

                        .anyRequest().authenticated());

        http.authenticationProvider(authenticationProvider(userDetailsService, passwordEncoder()));
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
