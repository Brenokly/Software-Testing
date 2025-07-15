package com.simulador.criaturas.property;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.simulador.criaturas.domain.model.Guardian;

import net.jqwik.api.Assume;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.api.constraints.Positive;

public class GuardianPropertyTest {

    @Property
    void move_shouldNeverAlterGold(
            @ForAll @DoubleRange(min = -1.0, max = 1.0) double randomR
    ) {
        Guardian guardian = new Guardian(1);
        double initialGold = 1000.0;
        guardian.setGold(initialGold);

        guardian.move(randomR);

        assertEquals(initialGold, guardian.getGold());
    }

    @Property
    void move_shouldNotThrowException_forValidInputs(
            @ForAll @DoubleRange(min = -1, max = 1) double validRandomR
    ) {
        Guardian guardian = new Guardian(1);
        guardian.setGold(1000.0);

        assertDoesNotThrow(() -> {
            guardian.move(validRandomR);
        });
    }

    @Property
    void move_shouldAlwaysThrowException_forInvalidInputs(
            @ForAll double anyRandomR
    ) {
        Assume.that(Double.isNaN(anyRandomR) || anyRandomR < -1 || anyRandomR > 1);

        Guardian guardian = new Guardian(1);

        assertThrows(IllegalArgumentException.class, () -> {
            guardian.move(anyRandomR);
        });
    }

    @Property
    void stealGold_shouldResultInExactSum(
            @ForAll @Positive double initialGold,
            @ForAll @Positive double amountToSteal
    ) {
        Assume.that(Double.isFinite(amountToSteal));

        Guardian guardian = new Guardian(1);
        guardian.setGold(initialGold);

        guardian.stealGold(amountToSteal);

        assertEquals(initialGold + amountToSteal, guardian.getGold());
    }

    @Property
    void stealGold_shouldNotAlterGold_forInvalidAmounts(
            @ForAll double initialGold,
            @ForAll double amountToSteal
    ) {
        Assume.that(Double.isFinite(amountToSteal));

        Guardian guardian = new Guardian(1);
        guardian.setGold(initialGold);

        guardian.stealGold(amountToSteal);

        if (amountToSteal <= 0) {
            assertEquals(initialGold, guardian.getGold());
        } else {
            assertEquals(initialGold + amountToSteal, guardian.getGold());
        }
    }
}
