package com.simulador.criaturas.structural;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.simulador.criaturas.domain.model.User;

@DisplayName("Testes Estruturais (MC/DC) para os comportamentos de User")
public class UserTestF {

    private User user;

    @BeforeEach
    void setUp() {
        // Um estado inicial padrão para os testes
        user = new User(1L, "testuser", "password", 5, 10, 20);
    }

    // --- MÉTODOS SEM LÓGICA CONDICIONAL ---
    // (incrementScore, incrementSimulationsRun, Construtores)
    @Test
    @DisplayName("Cobre os métodos de lógica linear (construtores e incrementadores)")
    void deveExecutarMetodosDeLogicaLinear() {
        // Para métodos sem 'if' ou loops, basta chamá-los uma vez para obter 100% de cobertura de linha/ramo.

        // Testando construtores (verificando se não lançam exceções e inicializam)
        User user1 = new User();
        user1.setLogin("login");
        user1.setPassword("pass");
        user1.setAvatarId(1);

        assertNotNull(user1);

        User user2 = new User();
        user2.setLogin("login2");
        user2.setPassword("pass2");
        user2.setAvatarId(2);

        assertNotNull(user2);

        // Testando incrementadores
        int initialScore = user.getPontuation();
        user.incrementScore();
        assertEquals(initialScore + 1, user.getPontuation());

        int initialRuns = user.getSimulationsRun();
        user.incrementSimulationsRun();
        assertEquals(initialRuns + 1, user.getSimulationsRun());
    }

    // --- TESTES ESTRUTURAIS PARA O MÉTODO changeAvatar(newAvatarId) ---
    // Decisão: if (newAvatarId >= 0)
    @Test
    @DisplayName("changeAvatar: Cobre o caminho IF (condição verdadeira)")
    void changeAvatar_caminhoIfVerdadeiro_quandoIdEValido() {
        // Este teste força a execução do bloco DENTRO do 'if'.

        user.changeAvatar(10);

        assertEquals(10, user.getAvatarId());
    }

    @Test
    @DisplayName("changeAvatar: Cobre o caminho ELSE implícito (condição falsa)")
    void changeAvatar_caminhoIfFalso_quandoIdEInvalido() {
        // Este teste força a condição do 'if' a ser falsa, pulando o bloco de código.

        int initialAvatarId = user.getAvatarId();

        user.changeAvatar(-1);

        assertEquals(initialAvatarId, user.getAvatarId());
    }

    // --- TESTES ESTRUTURAIS PARA O MÉTODO getAverageSuccessRate() ---
    // Decisão: if (simulationsRun == 0)
    @Test
    @DisplayName("getAverageSuccessRate: Cobre o caminho IF (condição verdadeira)")
    void getAverageSuccessRate_caminhoIfVerdadeiro_quandoSimulationsRunEZero() {
        // Este teste força a execução do bloco 'if' que retorna 0.0.

        user.setSimulationsRun(0);

        double rate = user.getAverageSuccessRate();

        assertEquals(0.0, rate);
    }

    @Test
    @DisplayName("getAverageSuccessRate: Cobre o caminho ELSE (condição falsa)")
    void getAverageSuccessRate_caminhoElse_quandoSimulationsRunNaoEZero() {
        // Este teste força a condição do 'if' a ser falsa, executando o cálculo da divisão.
        // O setup inicial tem pontuation=10 e simulationsRun=20.

        double rate = user.getAverageSuccessRate();

        assertEquals(0.5, rate);
    }
}
