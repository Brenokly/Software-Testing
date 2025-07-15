package com.simulador.criaturas.structural;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.simulador.criaturas.domain.model.Guardian;

@DisplayName("Testes Estruturais (MC/DC) para os comportamentos de Guardian")
public class GuardianStructuralTest {

    private Guardian guardian;

    @BeforeEach
    public void setUp() {
        guardian = new Guardian(1);
        guardian.setGold(1000.0);
    }

    @Test
    @DisplayName("move: Cobre o caminho principal (else) onde o movimento ocorre")
    void move_shouldFollowHappyPath_whenFactorIsValid() {
        guardian.setX(100);
        guardian.move(0.5);
        assertEquals(600.0, guardian.getX());
    }

    @Test
    @DisplayName("move: Cobre o caminho da exceção, isolando a condição isNaN (C1)")
    void move_shouldFollowExceptionPath_whenFactorIsNaN() {
        assertThrows(IllegalArgumentException.class, () -> guardian.move(Double.NaN));
    }

    @Test
    @DisplayName("move: Cobre o caminho da exceção, isolando a condição < -1 (C2)")
    void move_shouldFollowExceptionPath_whenFactorIsLessThanMinusOne() {
        assertThrows(IllegalArgumentException.class, () -> guardian.move(-1.1));
    }

    @Test
    @DisplayName("move: Cobre o caminho da exceção, isolando a condição > 1 (C3)")
    void move_shouldFollowExceptionPath_whenFactorIsGreaterThanOne() {
        assertThrows(IllegalArgumentException.class, () -> guardian.move(1.1));
    }

    @Test
    @DisplayName("stealGold: Cobre o caminho principal (else final) onde o roubo ocorre")
    void stealGold_shouldFollowHappyPath_whenAmountIsValid() {
        guardian.stealGold(100.0);
        assertEquals(1100.0, guardian.getGold());
    }

    @Test
    @DisplayName("stealGold: Cobre o caminho da exceção para valor não finito")
    void stealGold_shouldFollowExceptionPath_whenAmountIsNotFinite() {
        assertThrows(IllegalArgumentException.class, () -> guardian.stealGold(Double.POSITIVE_INFINITY));
    }

    @Test
    @DisplayName("stealGold: Cobre o caminho de retorno zero, isolando a condição == 0")
    void stealGold_shouldFollowZeroReturnPath_whenAmountIsZero() {
        double result = guardian.stealGold(0.0);
        assertEquals(0.0, result);
        assertEquals(1000.0, guardian.getGold());
    }

    @Test
    @DisplayName("stealGold: Cobre o caminho de retorno zero, isolando a condição < 0")
    void stealGold_shouldFollowZeroReturnPath_whenAmountIsNegative() {
        double result = guardian.stealGold(-50.0);
        assertEquals(0.0, result);
        assertEquals(1000.0, guardian.getGold());
    }
}
