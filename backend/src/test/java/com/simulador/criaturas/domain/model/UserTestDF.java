package com.simulador.criaturas.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testes de Domínio/Fronteira para a classe User")
class UserTestDF {

    private User user;

    @BeforeEach
    public void setUp() {
        // Usamos o construtor completo para ter um estado conhecido
        user = new User(1L, "testuser", "password123", 5, 10, 20);
    }

    // --- CONSTRUTORES ---
    @Test
    @DisplayName("Deve inicializar o User com valores padrão usando o construtor de login e senha")
    void deveInicializarComValoresPadraoUsandoConstrutorPrincipal() {
        String login = "newUser";
        String password = "newPassword";

        User newUser = new User(login, password);

        assertEquals(login, newUser.getLogin());
        assertEquals(password, newUser.getPassword());
        assertEquals(0, newUser.getAvatarId(), "Avatar ID deveria iniciar com 0.");
        assertEquals(0, newUser.getPontuation(), "Pontuação deveria iniciar com 0.");
        assertEquals(0, newUser.getSimulationsRun(), "Simulações executadas deveriam iniciar com 0.");
    }

    @Test
    @DisplayName("Deve inicializar o User com avatar específico usando o construtor de três argumentos")
    void deveInicializarComAvatarEspecifico() {
        String login = "avatarUser";
        String password = "newPassword";
        int avatarId = 7;

        User newUser = new User(login, password, avatarId);

        assertEquals(login, newUser.getLogin());
        assertEquals(password, newUser.getPassword());
        assertEquals(avatarId, newUser.getAvatarId(), "Avatar ID deveria ser o especificado.");
        assertEquals(0, newUser.getPontuation());
        assertEquals(0, newUser.getSimulationsRun());
    }

    // --- MÉTODO incrementScore ---
    @Test
    @DisplayName("Deve incrementar a pontuação em 1")
    void deveIncrementarPontuacao() {
        int initialScore = user.getPontuation(); // pontuação inicial é 10

        user.incrementScore();

        assertEquals(initialScore + 1, user.getPontuation(), "A pontuação deveria ter sido incrementada em 1.");
    }

    // --- MÉTODO incrementSimulationsRun ---
    @Test
    @DisplayName("Deve incrementar as simulações executadas em 1")
    void deveIncrementarSimulacoesExecutadas() {
        int initialRuns = user.getSimulationsRun(); // simulações iniciais são 20

        user.incrementSimulationsRun();

        assertEquals(initialRuns + 1, user.getSimulationsRun(), "O número de simulações deveria ter sido incrementado em 1.");
    }

    // --- MÉTODO changeAvatar ---
    @Test
    @DisplayName("Deve alterar o avatar quando o ID é positivo (Teste de Partição Válida)")
    void deveAlterarAvatarComIdPositivo() {
        int newAvatarId = 10;

        user.changeAvatar(newAvatarId);

        assertEquals(newAvatarId, user.getAvatarId());
    }

    @Test
    @DisplayName("Deve alterar o avatar quando o ID é zero (Teste de Fronteira)")
    void deveAlterarAvatarComIdZero() {
        int newAvatarId = 0;

        user.changeAvatar(newAvatarId);

        assertEquals(newAvatarId, user.getAvatarId());
    }

    @Test
    @DisplayName("Não deve alterar o avatar quando o ID é negativo (Teste de Partição Inválida)")
    void naoDeveAlterarAvatarComIdNegativo() {
        int initialAvatarId = user.getAvatarId(); // avatar inicial é 5
        int newAvatarId = -1;

        user.changeAvatar(newAvatarId);

        assertEquals(initialAvatarId, user.getAvatarId(), "O ID do avatar não deveria ter mudado.");
    }

    // --- MÉTODO getAverageSuccessRate ---
    @Test
    @DisplayName("Deve calcular a taxa de sucesso corretamente quando há simulações")
    void deveCalcularTaxaDeSucessoCorretamente() {
        // O setup inicial é: pontuation = 10, simulationsRun = 20
        double expectedRate = 10.0 / 20.0; // 0.5

        double actualRate = user.getAverageSuccessRate();

        assertEquals(expectedRate, actualRate, "A taxa de sucesso média está incorreta.");
    }

    @Test
    @DisplayName("Deve retornar 0.0 quando não há simulações executadas (Teste de Fronteira)")
    void deveRetornarZeroQuandoNaoHaSimulacoes() {
        user.setSimulationsRun(0);
        user.setPontuation(5); // Mesmo com pontuação, a taxa deve ser 0 para evitar divisão por zero.

        double actualRate = user.getAverageSuccessRate();

        assertEquals(0.0, actualRate, "A taxa de sucesso deveria ser 0 se não houver simulações executadas.");
    }
}
