package com.simulador.criaturas.structural;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.simulador.criaturas.domain.model.CreatureUnit;

@DisplayName("Testes Estruturais (MC/DC) para os comportamentos de CreatureUnit")
public class CreatureUnitStructuralTest {

    private CreatureUnit creature;

    @BeforeEach
    void setUp() {
        creature = new CreatureUnit(1);
        creature.setGold(1000.0);
    }

    @Test
    @DisplayName("move: Cobre o caminho principal (else) onde o movimento ocorre")
    void move_shouldFollowHappyPath_whenFactorIsValid() {
        creature.setX(100);
        creature.move(0.5);
        assertEquals(600.0, creature.getX());
    }

    @Test
    @DisplayName("move: Cobre o caminho da exceção, isolando a condição isNaN (C1)")
    void move_shouldFollowExceptionPath_whenFactorIsNaN() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> creature.move(Double.NaN));
        assertEquals("Valor aleatório deve estar entre -1 e 1.", exception.getMessage());
    }

    @Test
    @DisplayName("move: Cobre o caminho da exceção, isolando a condição < -1 (C2)")
    void move_shouldFollowExceptionPath_whenFactorIsLessThanMinusOne() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> creature.move(-1.1));
        assertEquals("Valor aleatório deve estar entre -1 e 1.", exception.getMessage());
    }

    @Test
    @DisplayName("move: Cobre o caminho da exceção, isolando a condição > 1 (C3)")
    void move_shouldFollowExceptionPath_whenFactorIsGreaterThanOne() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> creature.move(1.1));
        assertEquals("Valor aleatório deve estar entre -1 e 1.", exception.getMessage());
    }

    @Test
    @DisplayName("stealGold: Cobre o caminho principal (else final) onde o roubo ocorre")
    void stealGold_shouldFollowHappyPath_whenAmountIsValid() {
        creature.stealGold(100.0);
        assertEquals(1100.0, creature.getGold());
    }

    @Test
    @DisplayName("stealGold: Cobre o caminho da exceção para valor não finito")
    void stealGold_shouldFollowExceptionPath_whenAmountIsNotFinite() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> creature.stealGold(Double.POSITIVE_INFINITY));
        assertEquals("Roubo inválido: valor roubado não pode ser infinito.", exception.getMessage());
    }

    @Test
    @DisplayName("stealGold: Cobre o caminho de retorno zero, isolando a condição == 0")
    void stealGold_shouldFollowZeroReturnPath_whenAmountIsZero() {
        double result = creature.stealGold(0.0);
        assertEquals(0.0, result);
        assertEquals(1000.0, creature.getGold());
    }

    @Test
    @DisplayName("stealGold: Cobre o caminho de retorno zero, isolando a condição < 0")
    void stealGold_shouldFollowZeroReturnPath_whenAmountIsNegative() {
        double result = creature.stealGold(-50.0);
        assertEquals(0.0, result);
        assertEquals(1000.0, creature.getGold());
    }

    @Test
    @DisplayName("loseGold: Cobre o caminho principal onde o ouro é perdido")
    void loseGold_shouldFollowHappyPath_withValidGoldAndPercentage() {
        creature.loseGold(0.5);
        assertEquals(500.0, creature.getGold());
    }

    @Test
    @DisplayName("loseGold: Cobre o caminho da exceção, isolando a condição <= 0")
    void loseGold_shouldFollowExceptionPath_whenPercentageIsZeroOrLess() {
        IllegalArgumentException exception_zero = assertThrows(IllegalArgumentException.class, () -> creature.loseGold(0.0));
        assertEquals("Percentual de perda deve estar entre 0 e 1 (exclusivo de 0).", exception_zero.getMessage());

        IllegalArgumentException exception_negative = assertThrows(IllegalArgumentException.class, () -> creature.loseGold(-0.1));
        assertEquals("Percentual de perda deve estar entre 0 e 1 (exclusivo de 0).", exception_negative.getMessage());
    }

    @Test
    @DisplayName("loseGold: Cobre o caminho da exceção, isolando a condição > 1")
    void loseGold_shouldFollowExceptionPath_whenPercentageIsGreaterThanOne() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> creature.loseGold(1.1));
        assertEquals("Percentual de perda deve estar entre 0 e 1 (exclusivo de 0).", exception.getMessage());
    }

    @Test
    @DisplayName("loseGold: Cobre o caminho de retorno zero quando o ouro inicial é zero")
    void loseGold_shouldFollowZeroReturnPath_whenInitialGoldIsZero() {
        creature.setGold(0.0);
        double result = creature.loseGold(0.5);
        assertEquals(0.0, result);
        assertEquals(0.0, creature.getGold());
    }
}
