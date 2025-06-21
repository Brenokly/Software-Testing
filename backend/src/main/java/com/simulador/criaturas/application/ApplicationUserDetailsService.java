package com.simulador.criaturas.application;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.simulador.criaturas.domain.port.out.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationUserDetailsService implements UserDetailsService {

    private final UserRepositoryPort userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Usa nossa porta de repositório para buscar nosso User do domínio
        com.simulador.criaturas.domain.model.User domainUser = userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        // 2. "Traduz" nosso User do domínio para o User do Spring Security
        //    O construtor aceita: username, password, e uma coleção de authorities (perfis/regras)
        return new org.springframework.security.core.userdetails.User(
                domainUser.getLogin(),
                domainUser.getPassword(),
                new ArrayList<>() // Deixamos a lista de permissões vazia por enquanto
        );
    }
}
