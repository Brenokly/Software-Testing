package com.simulador.criaturas.property;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;

import com.simulador.criaturas.domain.model.User;

import jakarta.validation.constraints.PositiveOrZero;
import net.jqwik.api.Assume;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Negative;

@DisplayName("Testes de Propriedade para a classe de domínio User")
public class UserPropertyTest {

    // --- PROPRIEDADES PARA incrementScore E incrementSimulationsRun ---
    @Property
    @DisplayName("Propriedade: incrementScore sempre adiciona 1 à pontuação")
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
    @DisplayName("Propriedade: incrementSimulationsRun sempre adiciona 1 às simulações executadas")
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
    @DisplayName("Propriedade: changeAvatar atualiza o ID para qualquer valor não-negativo")
    void changeAvatarDeveAtualizarParaIdValido(
            // Gera qualquer inteiro válido (0 ou maior)
            @ForAll @PositiveOrZero int newAvatarId
    ) {
        // Arrange
        User user = new User();

        // Act
        user.changeAvatar(newAvatarId);

        // Assert
        // A propriedade é que o avatarId final é sempre igual à entrada válida.
        assertEquals(newAvatarId, user.getAvatarId());
    }

    @Property
    @DisplayName("Propriedade: changeAvatar ignora qualquer valor negativo")
    void changeAvatarDeveIgnorarIdInvalido(
            // Gera qualquer inteiro estritamente negativo
            @ForAll @Negative int invalidAvatarId
    ) {
        // Arrange
        User user = new User();
        int initialAvatarId = user.getAvatarId(); // Guarda o valor inicial (que é 0)

        // Act
        user.changeAvatar(invalidAvatarId);

        // Assert
        // A propriedade é que o avatarId NUNCA muda se a entrada for inválida.
        assertEquals(initialAvatarId, user.getAvatarId());
    }

    // --- PROPRIEDADES PARA getAverageSuccessRate ---
    @Property
    @DisplayName("Propriedade: A taxa de sucesso média está sempre entre 0.0 e 1.0")
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
    @DisplayName("Propriedade: A taxa de sucesso é sempre 0.0 se não houver simulações")
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
