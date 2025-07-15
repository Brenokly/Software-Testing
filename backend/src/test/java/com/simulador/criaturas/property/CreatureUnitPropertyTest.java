package com.simulador.criaturas.property;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.simulador.criaturas.domain.model.CreatureUnit;

import net.jqwik.api.Assume;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.api.constraints.Positive;

public class CreatureUnitPropertyTest {

    @Property
    void move_shouldNeverAlterGold(
            @ForAll @DoubleRange(min = -1.0, max = 1.0) double randomR
    ) {
        CreatureUnit creature = new CreatureUnit(1);
        double initialGold = 1000.0;
        creature.setGold(initialGold);

        creature.move(randomR);

        assertEquals(initialGold, creature.getGold());
    }

    @Property
    void move_shouldNotThrowException_forValidInputs(
            @ForAll @DoubleRange(min = -1, max = 1) double validRandomR
    ) {
        CreatureUnit creature = new CreatureUnit(1);
        creature.setGold(1000.0);

        assertDoesNotThrow(() -> {
            creature.move(validRandomR);
        });
    }

    @Property
    void move_shouldAlwaysThrowException_forInvalidInputs(
            @ForAll double anyRandomR
    ) {
        Assume.that(Double.isNaN(anyRandomR) || anyRandomR < -1 || anyRandomR > 1);

        CreatureUnit creature = new CreatureUnit(1);

        assertThrows(IllegalArgumentException.class, () -> {
            creature.move(anyRandomR);
        });
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

        assertTrue(creature.getGold() >= 0, "O ouro se tornou negativo: " + creature.getGold());
    }

    @Property
    void loseGold_shouldThrowException_forInvalidPercentage(
            @ForAll double anyRandomPercentage
    ) {
        Assume.that(anyRandomPercentage <= 0 || anyRandomPercentage > 1);

        CreatureUnit creature = new CreatureUnit(1);
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

        CreatureUnit creature = new CreatureUnit(1);
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

        CreatureUnit creature = new CreatureUnit(1);
        creature.setGold(initialGold);

        creature.stealGold(amountToSteal);

        if (amountToSteal <= 0) {
            assertEquals(initialGold, creature.getGold());
        } else {
            assertEquals(initialGold + amountToSteal, creature.getGold());
        }
    }
}
