package com.example.backend_Employee.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpMethod.*;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Supprimer l'appel à http.cors() ici
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(GET, "/api/employees/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(POST, "/api/employees").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(this::extractAuthorities);
        return authenticationConverter;
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();
        Collection<GrantedAuthority> authorities = defaultConverter.convert(jwt);

        List<String> roles = jwt.getClaimAsMap("realm_access") != null
                ? (List<String>) jwt.getClaimAsMap("realm_access").get("roles")
                : List.of();

        List<SimpleGrantedAuthority> roleAuthorities = roles.stream()
                .map(role -> {
                    if (role.startsWith("ROLE_")) {
                        return new SimpleGrantedAuthority(role); // Évite le double "ROLE_"
                    } else {
                        return new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());
                    }
                })
                .collect(Collectors.toList());

        authorities.addAll(roleAuthorities);
        return authorities;
    }

    // Nouvelle manière de gérer CORS en Spring Security 6.x
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:4200"); // Autorise uniquement ton frontend
        config.addAllowedHeader("*");
        config.addAllowedMethod("*"); // Autorise toutes les méthodes HTTP
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public CorsConfigurationSource corsSource() {
        return corsConfigurationSource();  // On applique la configuration CORS ici
    }
}
