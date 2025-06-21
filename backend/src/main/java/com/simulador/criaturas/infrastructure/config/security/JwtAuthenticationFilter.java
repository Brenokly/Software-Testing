package com.simulador.criaturas.infrastructure.config.security;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Filtro customizado que intercepta todas as requisições para validar o token
 * JWT. Ele é executado uma vez por requisição.
 */
@Component // Marca como um bean do Spring para que possamos injetá-lo
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; // O nosso ApplicationUserDetailsService

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Extrair o cabeçalho de autorização
        final String authHeader = request.getHeader("Authorization");

        // 2. Se não houver cabeçalho ou não começar com "Bearer ", passa para o próximo filtro
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extrair o token JWT (removendo o prefixo "Bearer ")
        final String jwt = authHeader.substring(7);

        // 4. Extrair o login (username) de dentro do token
        final String username = jwtService.extractUsername(jwt);

        // 5. Se temos o username E o usuário ainda não está autenticado no contexto de segurança
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Carrega os detalhes do usuário a partir do banco de dados
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 7. Se o token for válido...
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 8. Cria um "token de autenticação" que o Spring Security entende
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Credenciais são nulas pois estamos usando token
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 9. ATUALIZA O CONTEXTO DE SEGURANÇA: O usuário está oficialmente autenticado para esta requisição!
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 10. Passa a requisição para o próximo filtro na cadeia
        filterChain.doFilter(request, response);
    }
}
