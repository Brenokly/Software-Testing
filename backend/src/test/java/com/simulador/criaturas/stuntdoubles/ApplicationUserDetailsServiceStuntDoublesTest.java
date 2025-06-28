package com.simulador.criaturas.stuntdoubles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.simulador.criaturas.application.ApplicationUserDetailsService;
import com.simulador.criaturas.domain.model.User;
import com.simulador.criaturas.domain.port.out.UserRepositoryPort;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes (Dublês/Estruturais) para o Serviço de Aplicação ApplicationUserDetailsService")
public class ApplicationUserDetailsServiceStuntDoublesTest {

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private ApplicationUserDetailsService userDetailsService;

    // --- MÉTODO loadUserByUsername ---
    @Test
    @DisplayName("loadUserByUsername: Deve retornar UserDetails quando o usuário é encontrado")
    void loadUserByUsername_caminhoDeSucesso_quandoUsuarioEncontrado() {
        // Este teste cobre o caminho onde o Optional retornado pelo repositório está presente,
        // e o .orElseThrow() NÃO é executado.

        // Arrange
        String login = "testuser";
        String encodedPassword = "encodedPassword123";
        User userDoDominio = new User(1L, login, encodedPassword, 1, 10, 20);

        // "Treina" o mock para encontrar e retornar nosso usuário de domínio.
        when(userRepository.findByLogin(login)).thenReturn(Optional.of(userDoDominio));

        // Act
        UserDetails userDetailsResult = userDetailsService.loadUserByUsername(login);

        // Assert
        assertNotNull(userDetailsResult, "UserDetails não deveria ser nulo.");
        assertEquals(userDoDominio.getLogin(), userDetailsResult.getUsername(), "O username deve ser o mesmo.");
        assertEquals(userDoDominio.getPassword(), userDetailsResult.getPassword(), "A senha deve ser a mesma.");
        assertTrue(userDetailsResult.getAuthorities().isEmpty(), "A lista de permissões deveria estar vazia.");
    }

    @Test
    @DisplayName("loadUserByUsername: Deve lançar UsernameNotFoundException quando o usuário não é encontrado")
    void loadUserByUsername_caminhoDeExcecao_quandoUsuarioNaoEncontrado() {
        // Este teste cobre o caminho onde o Optional está vazio,
        // forçando a execução do .orElseThrow().

        // Arrange
        String nonExistentLogin = "ghost";

        // "Treina" o mock para retornar um Optional vazio, simulando um usuário não encontrado.
        when(userRepository.findByLogin(nonExistentLogin)).thenReturn(Optional.empty());

        // Act & Assert
        // Verificamos se a exceção correta do Spring Security é lançada.
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(nonExistentLogin);
        });

        // Verificamos a mensagem da exceção para garantir que é informativa.
        assertTrue(exception.getMessage().contains("Usuário não encontrado: " + nonExistentLogin));
    }
}
