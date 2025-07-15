package com.simulador.criaturas.property;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.simulador.criaturas.domain.model.User;

import jakarta.validation.constraints.PositiveOrZero;
import net.jqwik.api.Assume;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Negative;
import net.jqwik.api.constraints.Positive;

public class UserPropertyTest {

    @Property
    void incrementScore_shouldAlwaysAddOne(
            @ForAll @PositiveOrZero int initialScore
    ) {
        User user = new User();
        user.setPontuation(initialScore);

        user.incrementScore();

        assertEquals(initialScore + 1, user.getPontuation());
    }

    @Property
    void incrementSimulationsRun_shouldAlwaysAddOne(
            @ForAll @PositiveOrZero int initialRuns
    ) {
        User user = new User();
        user.setSimulationsRun(initialRuns);

        user.incrementSimulationsRun();

        assertEquals(initialRuns + 1, user.getSimulationsRun());
    }

    @Property
    void changeAvatar_shouldUpdate_forValidId(
            @ForAll @Positive int validAvatarId
    ) {
        User user = new User();
        user.setAvatarId(999);

        user.changeAvatar(validAvatarId);

        assertEquals(validAvatarId, user.getAvatarId());
    }

    @Property
    void changeAvatar_shouldIgnore_forInvalidId(
            @ForAll @Negative int invalidAvatarId
    ) {
        User user = new User();
        int initialAvatarId = 999;
        user.setAvatarId(initialAvatarId);

        user.changeAvatar(invalidAvatarId);

        assertEquals(initialAvatarId, user.getAvatarId());
    }

    @Property
    void getAverageSuccessRate_shouldAlwaysBeBetweenZeroAndOne(
            @ForAll @IntRange(min = 0, max = 10000) int pontuation,
            @ForAll @IntRange(min = 0, max = 10000) int simulationsRun
    ) {
        Assume.that(pontuation <= simulationsRun);

        User user = new User();
        user.setPontuation(pontuation);
        user.setSimulationsRun(simulationsRun);

        double rate = user.getAverageSuccessRate();

        assertTrue(rate >= 0.0 && rate <= 1.0);
    }

    @Property
    void getAverageSuccessRate_shouldBeZero_whenNoSimulationsRun(
            @ForAll @PositiveOrZero int pontuation
    ) {
        User user = new User();
        user.setPontuation(pontuation);
        user.setSimulationsRun(0);

        double rate = user.getAverageSuccessRate();

        assertEquals(0.0, rate);
    }
}
