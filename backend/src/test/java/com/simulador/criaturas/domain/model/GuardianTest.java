package com.simulador.criaturas.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testes de Domínio/Fronteira para a classe Guardian")
public class GuardianTest {

    private Guardian guardian;

    @BeforeEach
    public void setUp() {
        guardian = new Guardian(1);
    }

    @Test
    @DisplayName("Deve mover para frente corretamente quando o fator R é positivo")
    void move_shouldMoveForward_whenFactorIsPositive() {
        guardian.setGold(1000.0);
        guardian.setX(50.0);
        double expectedPosition = 50.0 + (0.5 * 1000.0);

        guardian.move(0.5);

        assertEquals(expectedPosition, guardian.getX());
    }

    @Test
    @DisplayName("Deve ajustar a posição para 0 quando o movimento resultaria em um X negativo")
    void move_shouldClampToZero_whenResultIsNegative() {
        guardian.setGold(500.0);
        guardian.setX(200.0);
        double expectedPosition = 0.0;

        guardian.move(-1.0);

        assertEquals(expectedPosition, guardian.getX());
    }

    @Test
    @DisplayName("Deve mover para trás corretamente sem cruzar a fronteira de zero")
    void move_shouldMoveBackward_withoutCrossingZero() {
        guardian.setGold(100.0);
        guardian.setX(200.0);
        double expectedPosition = 200.0 + (-1.0 * 100.0);

        guardian.move(-1.0);

        assertEquals(expectedPosition, guardian.getX());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para fator R maior que 1")
    void move_shouldThrowException_whenFactorIsGreaterThanOne() {
        double invalidFactor = 1.1;
        String expectedErrorMessage = "Valor aleatório deve estar entre -1 e 1.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            guardian.move(invalidFactor);
        });

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para fator R menor que -1")
    void move_shouldThrowException_whenFactorIsLessThanMinusOne() {
        double invalidFactor = -1.5;
        String expectedErrorMessage = "Valor aleatório deve estar entre -1 e 1.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            guardian.move(invalidFactor);
        });

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para fator R sendo NaN")
    void move_shouldThrowException_whenFactorIsNaN() {
        double invalidFactor = Double.NaN;
        String expectedErrorMessage = "Valor aleatório deve estar entre -1 e 1.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            guardian.move(invalidFactor);
        });

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Deve Roubar Ouro Corretamente Quando Quantidade Roubada é Positiva")
    void stealGold_shouldIncreaseGold_whenAmountIsPositive() {
        guardian.setGold(100.0);
        double amountToSteal = 50.0;
        double expectedGold = 150.0;

        double stolen = guardian.stealGold(amountToSteal);

        assertEquals(amountToSteal, stolen);
        assertEquals(expectedGold, guardian.getGold());
    }

    @Test
    @DisplayName("Deve Retornar Zero Quando Quantidade Roubada é Zero")
    void stealGold_shouldDoNothing_whenAmountIsZero() {
        guardian.setGold(100.0);
        double amountToSteal = 0.0;
        double expectedGold = 100.0;

        double stolen = guardian.stealGold(amountToSteal);

        assertEquals(0.0, stolen);
        assertEquals(expectedGold, guardian.getGold());
    }

    @Test
    @DisplayName("Deve Retornar Zero Quando Quantidade Roubada é Negativa")
    void stealGold_shouldDoNothing_whenAmountIsNegative() {
        guardian.setGold(100.0);
        double amountToSteal = -50.0;
        double expectedGold = 100.0;

        double stolen = guardian.stealGold(amountToSteal);

        assertEquals(0.0, stolen);
        assertEquals(expectedGold, guardian.getGold());
    }

    @Test
    @DisplayName("Deve Lançar IllegalArgumentException Quando Quantidade Roubada é Infinita")
    void stealGold_shouldThrowException_whenAmountIsInfinite() {
        guardian.setGold(100.0);
        double amountToSteal = Double.POSITIVE_INFINITY;
        String expectedErrorMessage = "Roubo inválido: valor roubado não pode ser infinito.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            guardian.stealGold(amountToSteal);
        });

        assertEquals(expectedErrorMessage, exception.getMessage());
    }
}
