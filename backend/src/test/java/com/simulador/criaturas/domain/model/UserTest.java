package com.simulador.criaturas.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testes de Domínio/Fronteira para a classe User")
class UserTest {

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User(1L, "testuser", "password123", 5, 10, 20);
    }

    @Test
    @DisplayName("Deve inicializar o User com valores padrão")
    void constructor_shouldInitializeWithDefaultValues() {
        String login = "newUser";
        String password = "newPassword";

        User newUser = new User();
        newUser.setLogin(login);
        newUser.setPassword(password);

        assertEquals(login, newUser.getLogin());
        assertEquals(password, newUser.getPassword());
        assertEquals(0, newUser.getAvatarId());
        assertEquals(0, newUser.getPontuation());
        assertEquals(0, newUser.getSimulationsRun());
    }

    @Test
    @DisplayName("Deve inicializar o User com avatar específico")
    void constructor_shouldInitializeWithSpecificAvatar() {
        String login = "avatarUser";
        String password = "newPassword";
        int avatarId = 7;

        User newUser = new User();
        newUser.setLogin(login);
        newUser.setPassword(password);
        newUser.setAvatarId(avatarId);

        assertEquals(login, newUser.getLogin());
        assertEquals(password, newUser.getPassword());
        assertEquals(avatarId, newUser.getAvatarId());
        assertEquals(0, newUser.getPontuation());
        assertEquals(0, newUser.getSimulationsRun());
    }

    @Test
    @DisplayName("Deve incrementar a pontuação em 1")
    void incrementScore_shouldIncrementPontuationByOne() {
        int initialScore = user.getPontuation();

        user.incrementScore();

        assertEquals(initialScore + 1, user.getPontuation());
    }

    @Test
    @DisplayName("Deve incrementar as simulações executadas em 1")
    void incrementSimulationsRun_shouldIncrementSimulationsByOne() {
        int initialRuns = user.getSimulationsRun();

        user.incrementSimulationsRun();

        assertEquals(initialRuns + 1, user.getSimulationsRun());
    }

    @Test
    @DisplayName("Deve alterar o avatar quando o ID é positivo")
    void changeAvatar_shouldChangeAvatar_whenIdIsPositive() {
        int newAvatarId = 10;

        user.changeAvatar(newAvatarId);

        assertEquals(newAvatarId, user.getAvatarId());
    }

    @Test
    @DisplayName("Deve alterar o avatar quando o ID é zero (Teste de Fronteira)")
    void changeAvatar_shouldChangeAvatar_whenIdIsZero() {
        int newAvatarId = 0;

        user.changeAvatar(newAvatarId);

        assertEquals(newAvatarId, user.getAvatarId());
    }

    @Test
    @DisplayName("Não deve alterar o avatar quando o ID é negativo")
    void changeAvatar_shouldNotChangeAvatar_whenIdIsNegative() {
        int initialAvatarId = user.getAvatarId();
        int newAvatarId = -1;

        user.changeAvatar(newAvatarId);

        assertEquals(initialAvatarId, user.getAvatarId());
    }

    @Test
    @DisplayName("Deve calcular a taxa de sucesso corretamente quando há simulações")
    void getAverageSuccessRate_shouldCalculateRateCorrectly_withSimulations() {
        double expectedRate = 10.0 / 20.0;

        double actualRate = user.getAverageSuccessRate();

        assertEquals(expectedRate, actualRate);
    }

    @Test
    @DisplayName("Deve retornar 0.0 quando não há simulações executadas (Teste de Fronteira)")
    void getAverageSuccessRate_shouldReturnZero_whenNoSimulationsRun() {
        user.setSimulationsRun(0);
        user.setPontuation(5);

        double actualRate = user.getAverageSuccessRate();

        assertEquals(0.0, actualRate);
    }
}
