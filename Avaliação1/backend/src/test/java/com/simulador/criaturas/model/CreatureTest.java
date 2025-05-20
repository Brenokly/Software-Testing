package com.simulador.criaturas.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreatureTest {

    // moveCreature()

    @Test
    void moveCreature_givenZero_shouldRemainAtSamePosition() {
        Creature c = new Creature(1); // x = 0, gold = 1_000_000
        c.moveCreature(0.0);
        assertThat(c.getX()).isEqualTo(0.0);
    }

    @Test
    void moveCreature_givenPositiveOne_shouldMoveToUpperBoundary() {
        Creature c = new Creature(1);
        c.moveCreature(1.0);
        assertThat(c.getX()).isEqualTo(1_000_000.0);
    }

    @Test
    void moveCreature_givenNegativeOne_shouldMoveToLowerBoundary() {
        Creature c = new Creature(1);
        c.moveCreature(-1.0);
        assertThat(c.getX()).isEqualTo(-1_000_000.0);
    }

    @Test
    void moveCreature_givenHalf_shouldMoveProportionally() {
        Creature c = new Creature(1);
        c.moveCreature(0.5);
        assertThat(c.getX()).isEqualTo(500_000.0);
    }

    @Test
    void moveCreature_givenNegativeOneWithCustomGold_shouldMoveToLowerBoundary() {
        Creature c = new Creature(2, 0.0, 1000000.0);
        c.moveCreature(-1.0);
        assertThat(c.getX()).isEqualTo(-1000000.0);
    }

    @Test
    void moveCreature_givenPositiveOneWithCustomGold_shouldMoveToUpperBoundary() {
        Creature c = new Creature(3, 5.0, 1500000.0);
        c.moveCreature(1.0);
        assertThat(c.getX()).isEqualTo(5.0 + 1500000.0);
    }

    @Test
    void moveCreature_givenValueAboveOne_shouldThrowException() {
        Creature c = new Creature(4, 0.0, 1000000.0);
        assertThatThrownBy(() -> c.moveCreature(1.1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Valor aleatório deve estar entre -1 e 1.");
    }

    @Test
    void moveCreature_givenValueBelowNegativeOne_shouldThrowException() {
        Creature c = new Creature(4, 0.0, 1000000.0);
        assertThatThrownBy(() -> c.moveCreature(-1.1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Valor aleatório deve estar entre -1 e 1.");
    }

    @Test
    void moveCreature_givenNaN_shouldThrowException() {
        Creature c = new Creature(1);
        assertThatThrownBy(() -> c.moveCreature(Double.NaN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Valor aleatório deve estar entre -1 e 1.");
    }

    @Test
    void moveCreature_givenInfinite_shouldThrowException() {
        Creature c = new Creature(1);

        assertThatThrownBy(() -> c.moveCreature(Double.POSITIVE_INFINITY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Valor aleatório deve estar entre -1 e 1.");

        assertThatThrownBy(() -> c.moveCreature(Double.NEGATIVE_INFINITY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Valor aleatório deve estar entre -1 e 1.");
    }

    // loseGold()

    @Test
    void loseGold_givenHalfPercentage_shouldDecreaseGoldByHalf() {
        Creature c = new Creature(7, 0.0, 1_000_000.0);
        double lost = c.loseGold(0.5);
        assertThat(lost).isEqualTo(500_000.0);
        assertThat(c.getGold()).isEqualTo(500_000.0);
    }

    @Test
    void loseGold_givenZeroGold_shouldLoseNothing() {
        Creature c = new Creature(6, 0.0, 0.0);
        double lost = c.loseGold(0.5);
        assertThat(lost).isZero();
        assertThat(c.getGold()).isZero();
    }

    @Test
    void loseGold_givenNegativeGold_shouldLoseNothing() {
        Creature c = new Creature(6, 0.0, -100.0);
        double lost = c.loseGold(0.7);
        assertThat(lost).isZero();
        assertThat(c.getGold()).isEqualTo(-100.0);
    }

    @Test
    void loseGold_givenPrecisionLimit_shouldNotLeaveNegativeGold() {
        Creature c = new Creature(1, 0.0, 1e-10);
        double lost = c.loseGold(1.0);
        assertThat(lost).isEqualTo(1e-10);
        assertThat(c.getGold()).isEqualTo(0.0);
    }

    @Test
    void loseGold_givenSmallPercentage_shouldDecreaseGoldCorrectly() {
        Creature c = new Creature(5, 0.0, 1_000_000.0);
        double lost = c.loseGold(0.0001);
        assertThat(lost).isEqualTo(100.0);
        assertThat(c.getGold()).isEqualTo(999_900.0);
    }

    @Test
    void loseGold_givenFullPercentage_shouldLoseAllGold() {
        Creature c = new Creature(5, 0.0, 1_000_000.0);
        double lost = c.loseGold(1.0);
        assertThat(lost).isEqualTo(1_000_000.0);
        assertThat(c.getGold()).isEqualTo(0.0);
    }

    @Test
    void loseGold_givenPercentageAboveOne_shouldThrowException() {
        Creature c = new Creature(5, 0.0, 1_000_000.0);
        assertThatThrownBy(() -> c.loseGold(1.2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Percentual de perda deve estar entre 0 e 1 (exclusivo de 0).");
    }

    @Test
    void loseGold_givenZeroPercentage_shouldThrowException() {
        Creature c = new Creature(6, 0.0, 1_000_000.0);
        assertThatThrownBy(() -> c.loseGold(0.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Percentual de perda deve estar entre 0 e 1 (exclusivo de 0).");
    }

    @Test
    void loseGold_givenNegativePercentage_shouldThrowException() {
        Creature c = new Creature(7, 0.0, 1_000_000.0);
        assertThatThrownBy(() -> c.loseGold(-0.5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Percentual de perda deve estar entre 0 e 1 (exclusivo de 0).");
    }

    @Test
    void loseGold_whenGoldIsZero_shouldNotEnterCalculationLogic() {
        Creature c = new Creature(123, 0.0, 0.0);

        double lost = c.loseGold(0.5);

        // Se entrou na lógica de cálculo, o gold teria mudado.
        // Se não entrou, o gold permanece zero.
        assertThat(c.getGold()).isEqualTo(0.0);
        assertThat(lost).isEqualTo(0.0);
    }

    // stealGoldFrom()

    @Test
    void stealGoldFrom_givenValidVictimAndHalfPercentage_shouldTransferGold() {
        Creature thief = new Creature(8); // x = 0, gold = 1_000_000.0
        Creature victim = new Creature(9); // x = 0, gold = 1_000_000.0
        double stolen = thief.stealGoldFrom(victim, 0.5);
        assertThat(stolen).isEqualTo(500_000.0);
        assertThat(thief.getGold()).isEqualTo(1_500_000.0);
        assertThat(victim.getGold()).isEqualTo(500_000.0);
    }

    @Test
    void stealGoldFrom_givenValidVictimWithSpecificGold_shouldTransferGold() {
        Creature thief = new Creature(10, 0.0, 3752_000.0);
        Creature victim = new Creature(11, 0.0, 289_723.0);
        double stolen = thief.stealGoldFrom(victim, 0.3);
        assertThat(stolen).isEqualTo(86_916.9);
        assertThat(thief.getGold()).isEqualTo(3838_916.9);
        assertThat(victim.getGold()).isEqualTo(202_806.1);
    }

    @Test
    void stealGoldFrom_givenVictimAndFullPercentage_shouldTransferAllGold() {
        Creature thief = new Creature(10);
        Creature victim = new Creature(11);
        double stolen = thief.stealGoldFrom(victim, 1.0);
        assertThat(stolen).isEqualTo(1_000_000.0);
        assertThat(thief.getGold()).isEqualTo(2_000_000.0);
        assertThat(victim.getGold()).isZero();
    }

    @Test
    void stealGoldFrom_givenVictimAndLowPercentage_shouldTransferPartialGold() {
        Creature thief = new Creature(12);
        Creature victim = new Creature(13);
        double stolen = thief.stealGoldFrom(victim, 0.1);
        assertThat(stolen).isEqualTo(100_000.0);
        assertThat(thief.getGold()).isEqualTo(1_100_000.0);
        assertThat(victim.getGold()).isEqualTo(900_000.0);
    }

    @Test
    void stealGoldFrom_givenNullVictim_shouldThrowException() {
        Creature thief = new Creature(10);
        assertThatThrownBy(() -> thief.stealGoldFrom(null, 0.5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Vítima não pode ser nula.");
    }

    @Test
    void stealGoldFrom_givenZeroPercentage_shouldThrowException() {
        Creature thief = new Creature(11, 0.0, 100.0);
        Creature victim = new Creature(12, 0.0, 100.0);
        assertThatThrownBy(() -> thief.stealGoldFrom(victim, 0.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Percentual de roubo deve estar entre 0 e 1 (exclusivo de 0).");
    }

    @Test
    void stealGoldFrom_givenNegativePercentage_shouldThrowException() {
        Creature thief = new Creature(11, 0.0, 100.0);
        Creature victim = new Creature(12, 0.0, 100.0);
        assertThatThrownBy(() -> thief.stealGoldFrom(victim, -0.5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Percentual de roubo deve estar entre 0 e 1 (exclusivo de 0).");
    }

    @Test
    void stealGoldFrom_givenPercentageAboveOne_shouldThrowException() {
        Creature thief = new Creature(11, 0.0, 100.0);
        Creature victim = new Creature(12, 0.0, 100.0);
        assertThatThrownBy(() -> thief.stealGoldFrom(victim, 1.5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Percentual de roubo deve estar entre 0 e 1 (exclusivo de 0).");
    }

    @Test
    void stealGoldFrom_givenVictimWithNoGold_shouldTransferNothing() {
        Creature thief = new Creature(13, 0.0, 100.0);
        Creature victim = new Creature(14, 0.0, 0.0);
        double stolen = thief.stealGoldFrom(victim, 0.5);
        assertThat(stolen).isZero();
        assertThat(thief.getGold()).isEqualTo(100.0);
        assertThat(victim.getGold()).isZero();
    }

    // Esses dois testes abaixo eu utilizei o Mockito para simular o comportamento
    // do método loseGold() á que ele nunca retorna NaN ou Infinity.

    @Test
    void stealGoldFrom_givenVictimReturnsNaN_shouldNotAddNaNToGold() {
        Creature thief = new Creature(1, 0.0, 100.0);
        Creature victim = mock(Creature.class);
        when(victim.loseGold(anyDouble())).thenReturn(Double.NaN);

        assertThatThrownBy(() -> thief.stealGoldFrom(victim, 0.5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Roubo inválido: valor roubado não pode ser negativo ou infinito.");

        assertThat(thief.getGold()).isEqualTo(100.0);
        assertThat(victim.getGold()).isEqualTo(0.0);
    }

    @Test
    void stealGoldFrom_givenVictimReturnsInfinity_shouldNotAddInfinityToGold() {
        Creature thief = new Creature(1, 0.0, 100.0);
        Creature victim = mock(Creature.class);
        when(victim.loseGold(anyDouble())).thenReturn(Double.POSITIVE_INFINITY);

        assertThatThrownBy(() -> thief.stealGoldFrom(victim, 0.5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Roubo inválido: valor roubado não pode ser negativo ou infinito.");

        assertThat(thief.getGold()).isEqualTo(100.0);
        assertThat(victim.getGold()).isEqualTo(0.0);
    }

    @Test
    void stealGoldFrom_givenVictimReturnsNegativeValue_shouldNotAddToGold() {
        Creature thief = new Creature(1, 0.0, 100.0);
        Creature victim = mock(Creature.class);
        when(victim.loseGold(anyDouble())).thenReturn(-42.0);

        assertThatThrownBy(() -> thief.stealGoldFrom(victim, 0.5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Roubo inválido: valor roubado não pode ser negativo ou infinito.");

        assertThat(thief.getGold()).isEqualTo(100.0);
    }

}
