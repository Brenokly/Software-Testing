package com.simulador.criaturas.stuntdoubles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.simulador.criaturas.application.UserService;
import com.simulador.criaturas.domain.model.User;
import com.simulador.criaturas.domain.port.out.UserRepositoryPort;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes (Dublês/Estruturais) para o Serviço de Aplicação UserService")
public class UserServiceTestStuntDoubles {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // --- MÉTODO registerNewUser ---
    @Test
    @DisplayName("registerNewUser: Deve registrar com sucesso (Caminho Principal)")
    void registerNewUser_caminhoPrincipal_quandoDadosSaoValidos() {
        when(userRepository.existsByLogin("newUser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.registerNewUser("newUser", "password123", 1);

        assertNotNull(result);
        assertEquals("newUser", result.getLogin());
        assertEquals("encodedPass", result.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("registerNewUser: Deve lançar exceção para login nulo ou vazio")
    void registerNewUser_caminhoDeExcecao_quandoLoginInvalido() {
        assertThrows(IllegalArgumentException.class, () -> userService.registerNewUser(null, "password", 1));
        assertThrows(IllegalArgumentException.class, () -> userService.registerNewUser(" ", "password", 1));
    }

    @Test
    @DisplayName("registerNewUser: Deve lançar exceção para senha nula ou vazia")
    void registerNewUser_caminhoDeExcecao_quandoSenhaInvalida() {
        assertThrows(IllegalArgumentException.class, () -> userService.registerNewUser("user", null, 1));
        assertThrows(IllegalArgumentException.class, () -> userService.registerNewUser("user", "  ", 1));
    }

    @Test
    @DisplayName("registerNewUser: Deve lançar exceção quando login já existe")
    void registerNewUser_caminhoDeExcecao_quandoLoginJaExiste() {
        when(userRepository.existsByLogin("existingUser")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.registerNewUser("existingUser", "password", 1));
        verify(userRepository, never()).save(any(User.class));
    }

    // --- MÉTODO authenticateUser ---
    @Test
    @DisplayName("authenticateUser: Deve retornar usuário quando credenciais estão corretas")
    void authenticateUser_caminhoDeSucesso() {
        String login = "user";
        String rawPass = "pass123";
        String encodedPass = "encodedPassABC";
        User userFromDb = new User(1L, login, encodedPass, 1, 0, 0);

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(userFromDb));
        when(passwordEncoder.matches(rawPass, encodedPass)).thenReturn(true);

        Optional<User> result = userService.authenticateUser(login, rawPass);

        assertTrue(result.isPresent());
        assertEquals(login, result.get().getLogin());
    }

    @Test
    @DisplayName("authenticateUser: Deve retornar vazio quando senha está incorreta")
    void authenticateUser_caminhoOpcionalVazio_quandoSenhaIncorreta() {
        String login = "user";
        String rawPass = "wrongPass";
        String encodedPass = "encodedPassABC";
        User userFromDb = new User(1L, login, encodedPass, 1, 0, 0);

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(userFromDb));
        when(passwordEncoder.matches(rawPass, encodedPass)).thenReturn(false);

        Optional<User> result = userService.authenticateUser(login, rawPass);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("authenticateUser: Deve lançar exceção quando login é nulo")
    void authenticateUser_caminhoDeErro_quandoLoginENulo() {
        assertThrows(IllegalArgumentException.class, () -> userService.authenticateUser(null, "pass123"));
    }

    @Test
    @DisplayName("authenticateUser: Deve lançar exceção quando senha é nula")
    void authenticateUser_caminhoDeErro_quandoSenhaENula() {
        assertThrows(IllegalArgumentException.class, () -> userService.authenticateUser("user", null));
    }

    // --- MÉTODO deleteUser ---
    @Test
    @DisplayName("deleteUser: Deve deletar com sucesso quando o solicitante é o dono")
    void deleteUser_caminhoDeSucesso() {
        Long userId = 1L;
        String login = "userToDelete";
        User userToDelete = new User(userId, login, "pass", 1, 0, 0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userToDelete));
        // O mock para deleteById não precisa de 'when' porque o método é void.
        // Nós vamos verificar se ele foi chamado.

        assertDoesNotThrow(() -> userService.deleteUser(userId, login));

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("deleteUser: Deve lançar SecurityException quando o solicitante não é o dono")
    void deleteUser_caminhoDeErro_solicitanteNaoEDono() {
        Long userId = 1L;
        String ownerLogin = "ownerUser";
        String requesterLogin = "hacker";
        User userToDelete = new User(userId, ownerLogin, "pass", 1, 0, 0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userToDelete));

        assertThrows(SecurityException.class, () -> {
            userService.deleteUser(userId, requesterLogin);
        });

        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("deleteUser: Deve lançar exceção quando login do solicitante é nulo")
    void deleteUser_caminhoDeErro_quandoLoginDoSolicitanteENulo() {
        // Arrange
        Long userId = 1L;
        // Precisamos de um usuário falso para o findById funcionar
        User fakeUser = new User(userId, "anyLogin", "anyPass", 0, 0, 0);
        // "Treinamos" o mock para encontrar o usuário e deixar o código avançar
        when(userRepository.findById(userId)).thenReturn(Optional.of(fakeUser));

        // Act & Assert
        // Agora, a exceção lançada será garantidamente pela validação do 'requesterLogin'
        assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUser(userId, null);
        });
    }

    @Test
    @DisplayName("deleteUser: Deve lançar exceção quando login do solicitante está em branco")
    void deleteUser_caminhoDeErro_quandoLoginDoSolicitanteEVazio() {
        // Arrange
        Long userId = 1L;
        User fakeUser = new User(userId, "anyLogin", "anyPass", 0, 0, 0);
        when(userRepository.findById(userId)).thenReturn(Optional.of(fakeUser));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUser(userId, " ");
        });
    }

    @Test
    @DisplayName("deleteUser: Deve lançar exceção quando login do solicitante é uma string vazia")
    void deleteUser_caminhoDeErro_quandoLoginDoSolicitanteEStringVazia() {
        // Arrange
        Long userId = 1L;
        User fakeUser = new User(userId, "anyLogin", "anyPass", 0, 0, 0);
        when(userRepository.findById(userId)).thenReturn(Optional.of(fakeUser));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUser(userId, "");
        });
    }

    @Test
    @DisplayName("deleteUser: Deve lançar IllegalArgumentException quando ID é nulo")
    void deleteUser_caminhoDeErro_quandoIdENulo() {
        String requesterLogin = "user";
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(null, requesterLogin));
    }

    // -- TESTES PARA O MÉTODO findUserByLogin
    @Test
    @DisplayName("findUserByLogin: Deve retornar usuário quando login é encontrado")
    void findUserByLogin_caminhoDeSucesso_quandoLoginEncontrado() {
        String login = "existingUser";
        User user = new User(1L, login, "encodedPass", 1, 0, 0);
        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findUserByLogin(login);

        assertTrue(result.isPresent());
        assertEquals(login, result.get().getLogin());
    }

    @Test
    @DisplayName("findUserByLogin: Deve retornar vazio quando login não é encontrado")
    void findUserByLogin_caminhoDeSucesso_quandoLoginNaoEncontrado() {
        String login = "nonExistentUser";
        when(userRepository.findByLogin(login)).thenReturn(Optional.empty());

        Optional<User> result = userService.findUserByLogin(login);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findUserByLogin: Deve lançar exceção quando login é nulo")
    void findUserByLogin_caminhoDeErro_quandoLoginENulo() {
        assertThrows(IllegalArgumentException.class, () -> userService.findUserByLogin(null));
    }

    // --- TESTES PARA O MÉTODO AUXILIAR findUserByIdOrThrow (via métodos públicos) ---
    @Test
    @DisplayName("findUserByIdOrThrow: Deve lançar exceção quando ID é nulo")
    void findUserByIdOrThrow_caminhoDeErro_quandoIdENulo() {
        assertThrows(IllegalArgumentException.class, () -> userService.incrementScore(null));
    }

    @Test
    @DisplayName("findUserByIdOrThrow: Deve lançar exceção quando usuário não é encontrado")
    void findUserByIdOrThrow_caminhoDeErro_quandoUsuarioNaoEncontrado() {
        Long nonExistentId = 99L;
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Testamos o helper através de um método público que o utiliza, como incrementScore.
        assertThrows(IllegalArgumentException.class, () -> userService.incrementScore(nonExistentId));
    }

    // --- MÉTODO incrementScore ---
    @Test
    @DisplayName("incrementScore: Deve incrementar o score do usuário com sucesso")
    void incrementScore_caminhoDeSucesso() {
        Long userId = 1L;
        User user = new User(userId, "user", "pass", 1, 0, 0);

        assertEquals(0, user.getPontuation());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Mockito.doNothing().when(userRepository).update(any(User.class));

        assertDoesNotThrow(() -> userService.incrementScore(userId));

        assertEquals(1, user.getPontuation());
        verify(userRepository, times(1)).update(user);
    }

    // --- MÉTODO incrementSimulationsRun ---
    @Test
    @DisplayName("incrementSimulationsRun: Deve incrementar o número de simulações executadas com sucesso")
    void incrementSimulationsRun_caminhoDeSucesso() {
        Long userId = 1L;
        User user = new User(userId, "user", "pass", 1, 0, 0);

        assertEquals(0, user.getSimulationsRun());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Mockito.doNothing().when(userRepository).update(any(User.class));
        assertDoesNotThrow(() -> userService.incrementSimulationsRun(userId));

        assertEquals(1, user.getSimulationsRun());
    }
}
