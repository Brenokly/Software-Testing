package com.simulador.criaturas.infrastructure.config.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.simulador.criaturas.application.ApplicationUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final ApplicationUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // O AuthenticationProvider diz ao Spring como obter os usuários e verificar as senhas
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Usa nosso serviço customizado
        authProvider.setPasswordEncoder(passwordEncoder());   // Usa nosso codificador de senhas
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite requisições do seu frontend em desenvolvimento
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        // Permite os métodos HTTP mais comuns
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Permite todos os cabeçalhos (incluindo Authorization)
        configuration.setAllowedHeaders(List.of("*"));
        // Permite o envio de credenciais (cookies, tokens)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration); // Aplica a configuração a todos os endpoints /api/**
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Aplica a configuração de CORS que definimos acima
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Desabilita CSRF, que não é necessário para APIs REST com token
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                // Permite acesso público aos endpoints de registro e login
                .requestMatchers("/api/users/register", "/api/users/login").permitAll()
                // Exige autenticação para qualquer outra requisição
                .anyRequest().authenticated()
                )
                // Define a política de sessão como STATELESS, pois usamos JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                // Adiciona nosso filtro JWT para ser executado antes do filtro padrão
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
