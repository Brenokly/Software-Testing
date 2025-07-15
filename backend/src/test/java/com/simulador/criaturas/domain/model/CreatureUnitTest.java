package com.simulador.criaturas.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testes de Domínio/Fronteira para a classe CreatureUnit")
public class CreatureUnitTest {

    private CreatureUnit creature;

    @BeforeEach
    public void setUp() {
        creature = new CreatureUnit(1);
    }

    @Test
    @DisplayName("Deve mover para frente corretamente quando o fator R é positivo")
    void move_shouldMoveForward_whenFactorIsPositive() {
        creature.setGold(1000.0);
        creature.setX(50.0);
        double expectedPosition = 50.0 + (0.5 * 1000.0);

        creature.move(0.5);

        assertEquals(expectedPosition, creature.getX());
    }

    @Test
    @DisplayName("Deve ajustar a posição para 0 quando o movimento resultaria em um X negativo")
    void move_shouldClampToZero_whenResultIsNegative() {
        creature.setGold(500.0);
        creature.setX(200.0);
        double expectedPosition = 0.0;

        creature.move(-1.0);

        assertEquals(expectedPosition, creature.getX());
    }

    @Test
    @DisplayName("Deve mover para trás corretamente sem cruzar a fronteira de zero")
    void move_shouldMoveBackward_withoutCrossingZero() {
        creature.setGold(100.0);
        creature.setX(200.0);
        double expectedPosition = 200.0 + (-1.0 * 100.0);

        creature.move(-1.0);

        assertEquals(expectedPosition, creature.getX());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para fator R maior que 1")
    void move_shouldThrowException_whenFactorIsGreaterThanOne() {
        double invalidFactor = 1.1;
        String expectedErrorMessage = "Valor aleatório deve estar entre -1 e 1.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            creature.move(invalidFactor);
        });

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para fator R menor que -1")
    void move_shouldThrowException_whenFactorIsLessThanMinusOne() {
        double invalidFactor = -1.5;
        String expectedErrorMessage = "Valor aleatório deve estar entre -1 e 1.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            creature.move(invalidFactor);
        });

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para fator R sendo NaN")
    void move_shouldThrowException_whenFactorIsNaN() {
        double invalidFactor = Double.NaN;
        String expectedErrorMessage = "Valor aleatório deve estar entre -1 e 1.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            creature.move(invalidFactor);
        });

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Deve Roubar Ouro Corretamente Quando Quantidade Roubada é Positiva")
    void stealGold_shouldIncreaseGold_whenAmountIsPositive() {
        creature.setGold(100.0);
        double amountToSteal = 50.0;
        double expectedGold = 150.0;

        double stolen = creature.stealGold(amountToSteal);

        assertEquals(amountToSteal, stolen);
        assertEquals(expectedGold, creature.getGold());
    }

    @Test
    @DisplayName("Deve Retornar Zero Quando Quantidade Roubada é Zero")
    void stealGold_shouldDoNothing_whenAmountIsZero() {
        creature.setGold(100.0);
        double amountToSteal = 0.0;
        double expectedGold = 100.0;

        double stolen = creature.stealGold(amountToSteal);

        assertEquals(0.0, stolen);
        assertEquals(expectedGold, creature.getGold());
    }

    @Test
    @DisplayName("Deve Retornar Zero Quando Quantidade Roubada é Negativa")
    void stealGold_shouldDoNothing_whenAmountIsNegative() {
        creature.setGold(100.0);
        double amountToSteal = -50.0;
        double expectedGold = 100.0;

        double stolen = creature.stealGold(amountToSteal);

        assertEquals(0.0, stolen);
        assertEquals(expectedGold, creature.getGold());
    }

    @Test
    @DisplayName("Deve Lançar IllegalArgumentException Quando Quantidade Roubada é Infinita")
    void stealGold_shouldThrowException_whenAmountIsInfinite() {
        creature.setGold(100.0);
        double amountToSteal = Double.POSITIVE_INFINITY;
        String expectedErrorMessage = "Roubo inválido: valor roubado não pode ser infinito.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            creature.stealGold(amountToSteal);
        });

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Deve Perder Ouro Corretamente Quando Percentual é Válido")
    void loseGold_shouldLoseGoldCorrectly_whenPercentageIsValid() {
        creature.setGold(1000.0);
        double percentage = 0.2;
        double expectedLost = 200.0;
        double expectedRemainingGold = 800.0;

        double lost = creature.loseGold(percentage);

        assertEquals(expectedLost, lost);
        assertEquals(expectedRemainingGold, creature.getGold());
    }

    @Test
    @DisplayName("Deve Lançar IllegalArgumentException Quando Percentual é Menor ou Igual a Zero")
    void loseGold_shouldThrowException_whenPercentageIsZeroOrLess() {
        creature.setGold(1000.0);
        double invalidPercentage = 0.0;
        String expectedErrorMessage = "Percentual de perda deve estar entre 0 e 1 (exclusivo de 0).";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            creature.loseGold(invalidPercentage);
        });

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Deve Lançar IllegalArgumentException Quando Percentual é Maior que 1")
    void loseGold_shouldThrowException_whenPercentageIsGreaterThanOne() {
        creature.setGold(1000.0);
        double invalidPercentage = 1.5;
        String expectedErrorMessage = "Percentual de perda deve estar entre 0 e 1 (exclusivo de 0).";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            creature.loseGold(invalidPercentage);
        });

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Deve Retornar Zero Quando Ouro é Zero e Percentual é Válido")
    void loseGold_shouldReturnZero_whenInitialGoldIsZero() {
        creature.setGold(0.0);
        double percentage = 0.5;

        double lost = creature.loseGold(percentage);

        assertEquals(0.0, lost);
        assertEquals(0.0, creature.getGold());
    }

    @Test
    @DisplayName("Deve Lançar IllegalArgumentException Mesmo Quando Ouro é Zero e Percentual é Inválido")
    void loseGold_shouldThrowException_whenPercentageIsInvalidAndGoldIsZero() {
        creature.setGold(0.0);
        double invalidPercentage = 1.5;
        String expectedErrorMessage = "Percentual de perda deve estar entre 0 e 1 (exclusivo de 0).";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            creature.loseGold(invalidPercentage);
        });

        assertEquals(expectedErrorMessage, exception.getMessage());
    }
}
