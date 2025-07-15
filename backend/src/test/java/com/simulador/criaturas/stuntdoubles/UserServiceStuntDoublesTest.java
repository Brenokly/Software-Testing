package com.simulador.criaturas.stuntdoubles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.simulador.criaturas.application.UserService;
import com.simulador.criaturas.domain.model.User;
import com.simulador.criaturas.domain.port.out.UserRepositoryPort;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes (Dublês/Estruturais) para o Serviço de Aplicação UserService")
public class UserServiceStuntDoublesTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("registerNewUser: Deve registrar com sucesso (Caminho Principal)")
    void registerNewUser_shouldSucceed_whenDataIsValid() {
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
    void registerNewUser_shouldThrowException_whenLoginIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> userService.registerNewUser(null, "password", 1));
        assertThrows(IllegalArgumentException.class, () -> userService.registerNewUser(" ", "password", 1));
    }

    @Test
    @DisplayName("registerNewUser: Deve lançar exceção para senha nula ou vazia")
    void registerNewUser_shouldThrowException_whenPasswordIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> userService.registerNewUser("user", null, 1));
        assertThrows(IllegalArgumentException.class, () -> userService.registerNewUser("user", "  ", 1));
    }

    @Test
    @DisplayName("registerNewUser: Deve lançar exceção quando login já existe")
    void registerNewUser_shouldThrowException_whenLoginAlreadyExists() {
        when(userRepository.existsByLogin("existingUser")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.registerNewUser("existingUser", "password", 1));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("authenticateUser: Deve retornar usuário quando credenciais estão corretas")
    void authenticateUser_shouldSucceed_whenCredentialsAreCorrect() {
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
    void authenticateUser_shouldReturnEmpty_whenPasswordIsIncorrect() {
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
    @DisplayName("authenticateUser: [CORRIGIDO] Deve retornar vazio quando login é nulo")
    void authenticateUser_shouldReturnEmpty_whenLoginIsNull() {
        Optional<User> result = userService.authenticateUser(null, "pass123");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("authenticateUser: [CORRIGIDO] Deve retornar vazio quando senha é nula")
    void authenticateUser_shouldReturnEmpty_whenPasswordIsNull() {
        Optional<User> result = userService.authenticateUser("user", null);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("deleteUser: Deve deletar com sucesso quando o solicitante é o dono")
    void deleteUser_shouldSucceed_whenRequesterIsOwner() {
        Long userId = 1L;
        String login = "userToDelete";
        User userToDelete = new User(userId, login, "pass", 1, 0, 0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userToDelete));

        assertDoesNotThrow(() -> userService.deleteUser(userId, login));

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("deleteUser: Deve lançar SecurityException quando o solicitante não é o dono")
    void deleteUser_shouldThrowException_whenRequesterIsNotOwner() {
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
    void deleteUser_shouldThrowException_whenRequesterLoginIsNull() {
        Long userId = 1L;
        User fakeUser = new User(userId, "anyLogin", "anyPass", 0, 0, 0);
        when(userRepository.findById(userId)).thenReturn(Optional.of(fakeUser));

        assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUser(userId, null);
        });
    }

    @Test
    @DisplayName("deleteUser: Deve lançar exceção quando login do solicitante está em branco")
    void deleteUser_shouldThrowException_whenRequesterLoginIsBlank() {
        Long userId = 1L;
        User fakeUser = new User(userId, "anyLogin", "anyPass", 0, 0, 0);
        when(userRepository.findById(userId)).thenReturn(Optional.of(fakeUser));

        assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUser(userId, " ");
        });
    }

    @Test
    @DisplayName("deleteUser: Deve lançar exceção quando login do solicitante é uma string vazia")
    void deleteUser_shouldThrowException_whenRequesterLoginIsEmpty() {
        Long userId = 1L;
        User fakeUser = new User(userId, "anyLogin", "anyPass", 0, 0, 0);
        when(userRepository.findById(userId)).thenReturn(Optional.of(fakeUser));

        assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUser(userId, "");
        });
    }

    @Test
    void deleteUser_shouldThrowException_whenIdIsNull() {
        String requesterLogin = "user";
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(null, requesterLogin));
    }

    @Test
    void findUserByLogin_shouldSucceed_whenLoginIsFound() {
        String login = "existingUser";
        User user = new User(1L, login, "encodedPass", 1, 0, 0);
        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findUserByLogin(login);

        assertTrue(result.isPresent());
    }

    @Test
    void findUserByLogin_shouldReturnEmpty_whenLoginIsNull() {
        Optional<User> result = userService.findUserByLogin(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void findUserByIdOrThrow_shouldThrowException_whenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> userService.incrementScore(null));
    }

    @Test
    void findUserByIdOrThrow_shouldThrowException_whenUserIsNotFound() {
        Long nonExistentId = 99L;
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.incrementScore(nonExistentId));
    }

    @Test
    void incrementScore_shouldSucceed() {
        Long userId = 1L;
        User user = new User(userId, "user", "pass", 1, 0, 0);
        assertEquals(0, user.getPontuation());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.doNothing().when(userRepository).update(any(User.class));

        assertDoesNotThrow(() -> userService.incrementScore(userId));

        assertEquals(1, user.getPontuation());
        verify(userRepository, times(1)).update(user);
    }

    @Test
    void incrementSimulationsRun_shouldSucceed() {
        Long userId = 1L;
        User user = new User(userId, "user", "pass", 1, 0, 0);
        assertEquals(0, user.getSimulationsRun());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.doNothing().when(userRepository).update(any(User.class));

        assertDoesNotThrow(() -> userService.incrementSimulationsRun(userId));

        assertEquals(1, user.getSimulationsRun());
    }
}
