package com.simulador.criaturas.property;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.simulador.criaturas.domain.model.User;

import jakarta.validation.constraints.PositiveOrZero;
import net.jqwik.api.Assume;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Negative;
import net.jqwik.api.constraints.Positive;

public class UserPropertyTest {

    // --- PROPRIEDADES PARA incrementScore E incrementSimulationsRun ---
    @Property
    void incrementScoreSempreAdicionaUm(
            // Gera qualquer inteiro não-negativo para ser a pontuação inicial
            @ForAll @PositiveOrZero int initialScore
    ) {
        // Arrange
        User user = new User();
        user.setPontuation(initialScore);

        // Act
        user.incrementScore();

        // Assert
        // A propriedade é que o resultado é sempre o inicial + 1.
        assertEquals(initialScore + 1, user.getPontuation());
    }

    @Property
    void incrementSimulationsRunSempreAdicionaUm(
            @ForAll @PositiveOrZero int initialRuns
    ) {
        // Arrange
        User user = new User();
        user.setSimulationsRun(initialRuns);

        // Act
        user.incrementSimulationsRun();

        // Assert
        // A propriedade é que o resultado é sempre o inicial + 1.
        assertEquals(initialRuns + 1, user.getSimulationsRun());
    }

    // --- PROPRIEDADES PARA changeAvatar ---
    @Property
    void changeAvatarDeveAtualizarParaIdValido(
            @ForAll @Positive int validAvatarId
    ) {
        // Arrange
        User user = new User();
        user.setAvatarId(999); // Um valor inicial diferente

        // Act
        user.changeAvatar(validAvatarId);

        // Assert
        assertEquals(validAvatarId, user.getAvatarId());
    }

    @Property
    void changeAvatarDeveIgnorarIdInvalido(
            @ForAll @Negative int invalidAvatarId
    ) {
        // Arrange
        User user = new User();
        int initialAvatarId = 999;
        user.setAvatarId(initialAvatarId);

        // Act
        user.changeAvatar(invalidAvatarId);

        assertEquals(initialAvatarId, user.getAvatarId(), "O avatarId não deveria ter mudado para um valor negativo.");
    }

    // --- PROPRIEDADES PARA getAverageSuccessRate ---
    @Property
    void averageSuccessRateEstaSempreEntreZeroEUm(
            @ForAll @IntRange(min = 0, max = 10000) int pontuation,
            @ForAll @IntRange(min = 0, max = 10000) int simulationsRun
    ) {
        // Pré-condição: A pontuação nunca pode ser maior que o número de simulações.
        // Assume.that() diz ao jqwik para pular as tentativas que não cumprem esta regra.
        Assume.that(pontuation <= simulationsRun);

        // Arrange
        User user = new User();
        user.setPontuation(pontuation);
        user.setSimulationsRun(simulationsRun);

        // Act
        double rate = user.getAverageSuccessRate();

        // Assert
        // A propriedade (invariante) é que a taxa é sempre um percentual válido.
        assertTrue(rate >= 0.0 && rate <= 1.0, "A taxa de sucesso saiu do intervalo [0, 1]: " + rate);
    }

    @Property
    void averageSuccessRateEZeroQuandoNaoHaSimulacoes(
            // Gera qualquer pontuação para provar que ela não importa neste caso
            @ForAll @PositiveOrZero int pontuation
    ) {
        // Arrange
        User user = new User();
        user.setPontuation(pontuation);
        user.setSimulationsRun(0); // Força a condição de fronteira

        // Act
        double rate = user.getAverageSuccessRate();

        // Assert
        // A propriedade é que o resultado é sempre 0.0 para evitar divisão por zero.
        assertEquals(0.0, rate);
    }
}
