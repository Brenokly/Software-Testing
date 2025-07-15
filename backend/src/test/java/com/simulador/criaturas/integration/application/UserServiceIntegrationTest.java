package com.simulador.criaturas.integration.application;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.simulador.criaturas.application.UserService;
import com.simulador.criaturas.domain.model.User;
import com.simulador.criaturas.domain.port.out.UserRepositoryPort;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepositoryPort userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- Testes para registerNewUser ---
    @Test
    @DisplayName("registerNewUser: Deve salvar um novo usuário com a senha criptografada no banco de dados")
    void registerNewUser_shouldSaveUserWithEncodedPasswordInDatabase() {
        String login = "integration_user";
        String rawPassword = "password123";
        int avatarId = 5;

        User createdUser = userService.registerNewUser(login, rawPassword, avatarId);

        User userFromDb = userRepository.findById(createdUser.getId()).orElseThrow();

        assertThat(userFromDb).isNotNull();
        assertThat(userFromDb.getLogin()).isEqualTo(login);
        assertThat(userFromDb.getPassword()).isNotEqualTo(rawPassword);
        assertThat(passwordEncoder.matches(rawPassword, userFromDb.getPassword())).isTrue();
    }

    @Test
    @DisplayName("registerNewUser: Deve lançar exceção ao tentar registrar um login que já existe")
    void registerNewUser_shouldThrowException_whenLoginAlreadyExists() {
        String existingLogin = "duplicate_user";
        userService.registerNewUser(existingLogin, "password123", 1);

        assertThatThrownBy(() -> userService.registerNewUser(existingLogin, "another_password", 2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Erro: Login já está em uso.");
    }

    // --- Testes para authenticateUser ---
    @Test
    @DisplayName("authenticateUser: Deve autenticar com sucesso um usuário persistido com credenciais corretas")
    void authenticateUser_shouldAuthenticate_whenCredentialsAreCorrect() {
        String login = "auth_user";
        String rawPassword = "secure_password";
        userService.registerNewUser(login, rawPassword, 1);

        Optional<User> authenticatedUserOpt = userService.authenticateUser(login, rawPassword);

        assertThat(authenticatedUserOpt).isPresent();
        assertThat(authenticatedUserOpt.get().getLogin()).isEqualTo(login);
    }

    @Test
    @DisplayName("authenticateUser: Deve retornar vazio se a senha estiver incorreta")
    void authenticateUser_shouldReturnEmpty_whenPasswordIsIncorrect() {
        String login = "user_wrong_pass";
        userService.registerNewUser(login, "correct_password", 1);

        Optional<User> authenticatedUserOpt = userService.authenticateUser(login, "wrong_password");

        assertThat(authenticatedUserOpt).isNotPresent();
    }

    @Test
    @DisplayName("authenticateUser: Deve retornar vazio se o usuário não existir")
    void authenticateUser_shouldReturnEmpty_whenUserDoesNotExist() {
        Optional<User> authenticatedUserOpt = userService.authenticateUser("non_existent_user", "any_password");

        assertThat(authenticatedUserOpt).isNotPresent();
    }

    // --- Testes para incrementScore ---
    @Test
    @DisplayName("incrementScore: Deve incrementar a pontuação de um usuário no banco de dados")
    void incrementScore_shouldIncrementScoreInDatabase() {
        User user = userService.registerNewUser("score_user", "pass", 1);
        assertThat(user.getPontuation()).isZero();

        userService.incrementScore(user.getId());

        User userFromDb = userRepository.findById(user.getId()).orElseThrow();
        assertThat(userFromDb.getPontuation()).isEqualTo(1);
    }

    // --- Testes para incrementSimulationsRun ---
    @Test
    @DisplayName("incrementSimulationsRun: Deve incrementar as simulações de um usuário no banco de dados")
    void incrementSimulationsRun_shouldIncrementSimulationsInDatabase() {
        User user = userService.registerNewUser("sim_user", "pass", 1);
        assertThat(user.getSimulationsRun()).isZero();

        userService.incrementSimulationsRun(user.getId());

        User userFromDb = userRepository.findById(user.getId()).orElseThrow();
        assertThat(userFromDb.getSimulationsRun()).isEqualTo(1);
    }

    // --- Testes para findUserByLogin ---
    @Test
    @DisplayName("findUserByLogin: Deve encontrar e retornar um usuário existente")
    void findUserByLogin_shouldReturnUser_whenUserExists() {
        userService.registerNewUser("find_me_user", "pass", 1);

        Optional<User> result = userService.findUserByLogin("find_me_user");

        assertThat(result).isPresent();
        assertThat(result.get().getLogin()).isEqualTo("find_me_user");
    }

    // --- Testes para deleteUser ---
    @Test
    @DisplayName("deleteUser: Deve remover um usuário do banco de dados")
    void deleteUser_shouldRemoveUserFromDatabase() {
        User user = userService.registerNewUser("delete_me", "pass", 1);
        Long userId = user.getId();

        userService.deleteUser(userId, "delete_me");

        Optional<User> userFromDb = userRepository.findById(userId);
        assertThat(userFromDb).isNotPresent();
    }

    @Test
    @DisplayName("deleteUser: Deve lançar SecurityException e não remover o usuário")
    void deleteUser_shouldThrowExceptionAndNotDelete_whenRequesterIsNotOwner() {
        User user = userService.registerNewUser("dont_delete_me", "pass", 1);
        Long userId = user.getId();

        assertThatThrownBy(() -> userService.deleteUser(userId, "another_user"))
                .isInstanceOf(SecurityException.class);

        Optional<User> userFromDb = userRepository.findById(userId);
        assertThat(userFromDb).isPresent();
    }

    // --- Teste para findUserByIdOrThrow (via delegação) ---
    @Test
    @DisplayName("findUserByIdOrThrow: Deve lançar exceção ao tentar incrementar pontuação de usuário inexistente")
    void findUserByIdOrThrow_shouldThrowException_whenUserDoesNotExist() {
        assertThatThrownBy(() -> userService.incrementScore(9999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Usuário com ID 9999 não encontrado.");
    }
}
