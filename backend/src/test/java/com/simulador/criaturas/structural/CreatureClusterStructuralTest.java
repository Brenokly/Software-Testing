package com.simulador.criaturas.structural;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.simulador.criaturas.domain.model.CreatureCluster;
import com.simulador.criaturas.domain.model.CreatureUnit;

@DisplayName("Testes Estruturais (MC/DC) para os comportamentos de CreatureCluster")
public class CreatureClusterStructuralTest {

    private CreatureCluster creatureCluster;

    @BeforeEach
    void setUp() {
        creatureCluster = new CreatureCluster(100, 100.0, 1000.0);
    }

    @Test
    @DisplayName("fusion: Cobre o caminho principal com uma criatura válida")
    void fusion_shouldFollowHappyPath_whenCreatureIsValid() {
        CreatureUnit creatureParaAbsorver = new CreatureUnit(1, 0.0, 500.0);
        double expectedGold = creatureCluster.getGold() + creatureParaAbsorver.getGold();

        creatureCluster.fusion(creatureParaAbsorver);

        assertEquals(expectedGold, creatureCluster.getGold());
    }

    @Test
    @DisplayName("fusion: Cobre o caminho de exceção quando a criatura é nula")
    void fusion_shouldFollowExceptionPath_whenCreatureIsNull() {
        String expectedMessage = "Criatura não pode ser nula.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            creatureCluster.fusion(null);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("fusion: Cobre o caminho da exceção quando o ouro da criatura não é finito")
    void fusion_shouldFollowExceptionPath_whenCreatureGoldIsNotFinite() {
        CreatureUnit criaturaInvalida = new CreatureUnit(2, 0.0, Double.POSITIVE_INFINITY);
        String expectedMessage = "A quantidade de ouro da criatura deve ser um número finito.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            creatureCluster.fusion(criaturaInvalida);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("move: Cobre o caminho principal (else) onde o movimento ocorre")
    void move_shouldFollowHappyPath_whenFactorIsValid() {
        creatureCluster.setX(100);
        creatureCluster.move(0.5);
        assertEquals(600.0, creatureCluster.getX());
    }

    @Test
    @DisplayName("move: Cobre o caminho da exceção, isolando a condição isNaN (C1)")
    void move_shouldFollowExceptionPath_whenFactorIsNaN() {
        assertThrows(IllegalArgumentException.class, () -> creatureCluster.move(Double.NaN));
    }

    @Test
    @DisplayName("move: Cobre o caminho da exceção, isolando a condição < -1 (C2)")
    void move_shouldFollowExceptionPath_whenFactorIsLessThanMinusOne() {
        assertThrows(IllegalArgumentException.class, () -> creatureCluster.move(-1.1));
    }

    @Test
    @DisplayName("move: Cobre o caminho da exceção, isolando a condição > 1 (C3)")
    void move_shouldFollowExceptionPath_whenFactorIsGreaterThanOne() {
        assertThrows(IllegalArgumentException.class, () -> creatureCluster.move(1.1));
    }

    @Test
    @DisplayName("stealGold: Cobre o caminho principal (else final) onde o roubo ocorre")
    void stealGold_shouldFollowHappyPath_whenAmountIsValid() {
        creatureCluster.stealGold(100.0);
        assertEquals(1100.0, creatureCluster.getGold());
    }

    @Test
    @DisplayName("stealGold: Cobre o caminho da exceção para valor não finito")
    void stealGold_shouldFollowExceptionPath_whenAmountIsNotFinite() {
        assertThrows(IllegalArgumentException.class, () -> creatureCluster.stealGold(Double.POSITIVE_INFINITY));
    }

    @Test
    @DisplayName("stealGold: Cobre o caminho de retorno zero, isolando a condição == 0")
    void stealGold_shouldFollowZeroReturnPath_whenAmountIsZero() {
        double result = creatureCluster.stealGold(0.0);
        assertEquals(0.0, result);
        assertEquals(1000.0, creatureCluster.getGold());
    }

    @Test
    @DisplayName("stealGold: Cobre o caminho de retorno zero, isolando a condição < 0")
    void stealGold_shouldFollowZeroReturnPath_whenAmountIsNegative() {
        double result = creatureCluster.stealGold(-50.0);
        assertEquals(0.0, result);
        assertEquals(1000.0, creatureCluster.getGold());
    }

    @Test
    @DisplayName("loseGold: Cobre o caminho principal onde o ouro é perdido")
    void loseGold_shouldFollowHappyPath_withValidGoldAndPercentage() {
        creatureCluster.loseGold(0.5);
        assertEquals(500.0, creatureCluster.getGold());
    }

    @Test
    @DisplayName("loseGold: Cobre o caminho da exceção, isolando a condição <= 0")
    void loseGold_shouldFollowExceptionPath_whenPercentageIsZeroOrLess() {
        assertThrows(IllegalArgumentException.class, () -> creatureCluster.loseGold(0.0));
        assertThrows(IllegalArgumentException.class, () -> creatureCluster.loseGold(-0.1));
    }

    @Test
    @DisplayName("loseGold: Cobre o caminho da exceção, isolando a condição > 1")
    void loseGold_shouldFollowExceptionPath_whenPercentageIsGreaterThanOne() {
        assertThrows(IllegalArgumentException.class, () -> creatureCluster.loseGold(1.1));
    }

    @Test
    @DisplayName("loseGold: Cobre o caminho de retorno zero quando o ouro inicial é zero")
    void loseGold_shouldFollowZeroReturnPath_whenInitialGoldIsZero() {
        creatureCluster.setGold(0.0);
        double result = creatureCluster.loseGold(0.5);
        assertEquals(0.0, result);
        assertEquals(0.0, creatureCluster.getGold());
    }
}
