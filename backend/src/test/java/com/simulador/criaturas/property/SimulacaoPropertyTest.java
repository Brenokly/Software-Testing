package com.simulador.criaturas.property;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.simulador.criaturas.domain.behaviors.HorizonEntities;
import com.simulador.criaturas.domain.model.Horizon;
import com.simulador.criaturas.domain.port.out.RandomPort;
import com.simulador.criaturas.domain.service.Simulation;
import com.simulador.criaturas.utils.SimulationStatus;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.constraints.IntRange;

class SimulationPropertyTest {

    private static class FixedRandomPort implements RandomPort {

        private final double factor;

        FixedRandomPort(double factor) {
            this.factor = factor;
        }

        @Override
        public double nextFactor() {
            return factor;
        }
    }

    @Property
    void createNewSimulation_shouldAlwaysResultInValidState(
            @ForAll @IntRange(min = 1, max = 10) int numberOfCreatures
    ) {
        Simulation simulation = new Simulation(new FixedRandomPort(0.0));
        Horizon horizon = simulation.createNewSimulation(numberOfCreatures);

        assertNotNull(horizon);
        assertEquals(numberOfCreatures, horizon.getEntities().size());
        assertEquals(SimulationStatus.RUNNING, horizon.getStatus());
        assertNotNull(horizon.getGuardiao());
    }

    @Property
    void runIteration_shouldNotIncreaseEntityCount(
            @ForAll("validHorizonsForIteration") Horizon horizon
    ) {
        Simulation simulation = new Simulation(new FixedRandomPort(0.5));
        int initialSize = horizon.getEntities().size();

        simulation.runIteration(horizon);

        int finalSize = horizon.getEntities().size();
        assertTrue(finalSize <= initialSize);
    }

    @Property
    void runIteration_shouldAlwaysConserveTotalGold(
            @ForAll("validHorizonsForIteration") Horizon horizon
    ) {
        Simulation simulation = new Simulation(new FixedRandomPort(0.2));
        double initialTotalGold = calculateTotalGold(horizon);

        simulation.runIteration(horizon);

        double finalTotalGold = calculateTotalGold(horizon);
        assertEquals(initialTotalGold, finalTotalGold, 0.00001);
    }

    private double calculateTotalGold(Horizon horizon) {
        double entitiesGold = horizon.getEntities().stream()
                .mapToDouble(HorizonEntities::getGold)
                .sum();
        double guardianGold = (horizon.getGuardiao() != null) ? horizon.getGuardiao().getGold() : 0;
        return entitiesGold + guardianGold;
    }

    @Provide
    Arbitrary<Horizon> validHorizonsForIteration() {
        Arbitrary<Integer> size = Arbitraries.integers().between(2, 15);

        return size.map(s -> {
            int validSize = Math.min(s, 10);
            Simulation tempSim = new Simulation(new FixedRandomPort(0));
            Horizon horizon = tempSim.createNewSimulation(validSize);

            for (HorizonEntities entity : horizon.getEntities()) {
                entity.setGold(Arbitraries.doubles().between(100, 10000).sample());
                entity.setX(Arbitraries.integers().between(0, 5).sample() * 100.0);
            }
            if (horizon.getGuardiao() != null) {
                horizon.getGuardiao().setGold(Arbitraries.doubles().between(0, 5000).sample());
                horizon.getGuardiao().setX(Arbitraries.integers().between(0, 5).sample() * 100.0);
            }
            horizon.setStatus(SimulationStatus.RUNNING);
            return horizon;
        });
    }
}
