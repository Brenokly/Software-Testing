package com.simulador.criaturas.property;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.simulador.criaturas.domain.behaviors.HorizonEntities;
import com.simulador.criaturas.domain.model.CreatureCluster;
import com.simulador.criaturas.domain.model.CreatureUnit;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Assume;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.api.constraints.Positive;

public class CreatureClusterPropertyTest {

    @Property
    void move_shouldNeverAlterGold(
            @ForAll @DoubleRange(min = -1.0, max = 1.0) double randomR
    ) {
        CreatureCluster cluster = new CreatureCluster(1, 100.0, 1000.0);
        double initialGold = cluster.getGold();
        cluster.move(randomR);
        assertEquals(initialGold, cluster.getGold());
    }

    @Property
    void move_shouldNotThrowException_forValidInputs(
            @ForAll @DoubleRange(min = -1, max = 1) double validRandomR
    ) {
        CreatureCluster cluster = new CreatureCluster(1, 100.0, 1000.0);
        assertDoesNotThrow(() -> cluster.move(validRandomR));
    }

    @Property
    void move_shouldAlwaysThrowException_forInvalidInputs(
            @ForAll double anyRandomR
    ) {
        Assume.that(Double.isNaN(anyRandomR) || anyRandomR < -1 || anyRandomR > 1);
        CreatureCluster cluster = new CreatureCluster(1, 100.0, 1000.0);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> cluster.move(anyRandomR));
        assertEquals("Valor aleatório deve estar entre -1 e 1.", exception.getMessage());
    }

    @Property
    void move_shouldAlwaysResultInNonNegativePosition(
            @ForAll @DoubleRange(min = -1, max = 1) double randomR,
            @ForAll double initialX,
            @ForAll @Positive double gold
    ) {
        CreatureCluster cluster = new CreatureCluster(1, initialX, gold);
        cluster.move(randomR);
        assertTrue(cluster.getX() >= 0);
    }

    @Property
    void loseGold_shouldNeverResultInNegativeGold(
            @ForAll @Positive double initialGold,
            @ForAll @DoubleRange(min = 0.0, max = 1.0, maxIncluded = true) double validPercentage
    ) {
        Assume.that(validPercentage > 0);
        CreatureUnit creature = new CreatureUnit(1);
        creature.setGold(initialGold);
        creature.loseGold(validPercentage);
        assertTrue(creature.getGold() >= 0);
    }

    @Property
    void loseGold_shouldThrowException_forInvalidPercentage(
            @ForAll double anyRandomPercentage
    ) {
        Assume.that(anyRandomPercentage <= 0 || anyRandomPercentage > 1);
        CreatureCluster creature = new CreatureCluster(1);
        creature.setGold(1000.0);
        assertThrows(IllegalArgumentException.class, () -> {
            creature.loseGold(anyRandomPercentage);
        });
    }

    @Property
    void stealGold_shouldResultInExactSum(
            @ForAll @Positive double initialGold,
            @ForAll @Positive double amountToSteal
    ) {
        Assume.that(Double.isFinite(amountToSteal));
        CreatureCluster creature = new CreatureCluster(1);
        creature.setGold(initialGold);
        creature.stealGold(amountToSteal);
        assertEquals(initialGold + amountToSteal, creature.getGold());
    }

    @Property
    void stealGold_shouldNotAlterGold_forInvalidAmounts(
            @ForAll double initialGold,
            @ForAll double amountToSteal
    ) {
        Assume.that(Double.isFinite(amountToSteal));
        CreatureCluster creature = new CreatureCluster(1);
        creature.setGold(initialGold);
        creature.stealGold(amountToSteal);
        if (amountToSteal <= 0) {
            assertEquals(initialGold, creature.getGold());
        } else {
            assertEquals(initialGold + amountToSteal, creature.getGold());
        }
    }

    @Property
    void fusion_shouldResultInExactSum(
            @ForAll @Positive double clusterInitialGold,
            @ForAll @Positive double creatureGold
    ) {
        Assume.that(Double.isFinite(clusterInitialGold) && Double.isFinite(creatureGold));
        CreatureCluster cluster = new CreatureCluster(1, 0.0, clusterInitialGold);
        CreatureUnit creatureToAbsorb = new CreatureUnit(2, 0.0, creatureGold);
        double expectedFinalGold = clusterInitialGold + creatureGold;
        cluster.fusion(creatureToAbsorb);
        assertEquals(expectedFinalGold, cluster.getGold(), 0.000001);
    }

    @Property
    void fusion_shouldAlwaysThrowException_forInvalidInputs(
            @ForAll("invalidCreaturesForFusion") HorizonEntities invalidCreature
    ) {
        CreatureCluster cluster = new CreatureCluster(1, 0.0, 1000.0);
        assertThrows(IllegalArgumentException.class, () -> {
            cluster.fusion(invalidCreature);
        });
    }

    @Provide
    Arbitrary<HorizonEntities> invalidCreaturesForFusion() {
        CreatureUnit creatureWithInfiniteGold = new CreatureUnit(99);
        creatureWithInfiniteGold.setGold(Double.POSITIVE_INFINITY);
        CreatureUnit creatureWithNaNGold = new CreatureUnit(98);
        creatureWithNaNGold.setGold(Double.NaN);
        return Arbitraries.of(null, creatureWithInfiniteGold, creatureWithNaNGold);
    }
}
