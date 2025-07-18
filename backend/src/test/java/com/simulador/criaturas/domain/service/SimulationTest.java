package com.simulador.criaturas.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.simulador.criaturas.domain.behaviors.HorizonEntities;
import com.simulador.criaturas.domain.model.CreatureCluster;
import com.simulador.criaturas.domain.model.CreatureUnit;
import com.simulador.criaturas.domain.model.Guardian;
import com.simulador.criaturas.domain.model.Horizon;
import com.simulador.criaturas.domain.port.out.RandomPort;
import com.simulador.criaturas.utils.SimulationStatus;

@DisplayName("Testes de Domínio/Fronteira para a classe Simulation (Serviço de Domínio)")
class SimulationTest {

    private Simulation simulation;
    private RandomPort deterministicRandomPort;

    @BeforeEach
    public void setUp() {
        deterministicRandomPort = new DeterministicRandomPort(0.1);
        simulation = new Simulation(deterministicRandomPort);
    }

    private static class DeterministicRandomPort implements RandomPort {

        private final double factorToReturn;

        public DeterministicRandomPort(double factor) {
            this.factorToReturn = factor;
        }

        @Override
        public double nextFactor() {
            return factorToReturn;
        }
    }

    @Test
    @DisplayName("getStatus: Deve retornar SUCCESSFUL quando só resta o guardião")
    void getStatus_shouldReturnSuccessful_whenOnlyGuardianRemains() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));

        horizon.getEntities().clear();

        SimulationStatus status = simulation.getStatus(horizon);

        assertEquals(SimulationStatus.SUCCESSFUL, status);
    }

    @Test
    @DisplayName("getStatus: Deve retornar SUCCESSFUL quando resta 1 criatura e o guardião tem mais ouro")
    void getStatus_shouldReturnSuccessful_whenGuardianHasMoreGold() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));

        horizon.getGuardiao().setGold(1000);
        horizon.getEntities().get(0).setGold(500);

        SimulationStatus status = simulation.getStatus(horizon);

        assertEquals(SimulationStatus.SUCCESSFUL, status);
    }

    @Test
    @DisplayName("getStatus: Deve retornar FAILED quando resta 1 criatura e o guardião tem menos ouro")
    void getStatus_shouldReturnFailed_whenGuardianHasLessGold() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));

        horizon.getGuardiao().setGold(500);
        horizon.getEntities().get(0).setGold(1000);

        SimulationStatus status = simulation.getStatus(horizon);

        assertEquals(SimulationStatus.FAILED, status);
    }

    @Test
    @DisplayName("getStatus: Deve retornar RUNNING quando há múltiplas criaturas")
    void getStatus_shouldReturnRunning_whenMultipleCreaturesExist() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.setGuardiao(new Guardian(4));

        SimulationStatus status = simulation.getStatus(horizon);

        assertEquals(SimulationStatus.RUNNING, status);
    }

    @Test
    @DisplayName("runIteration: Deve fundir duas criaturas que se movem para a mesma posição")
    void runIteration_shouldFuseTwoCreatures_whenTheyCollide() {
        simulation = new Simulation(new DeterministicRandomPort(0.5));
        Horizon horizon = new Horizon();
        horizon.initializeEntities(2);
        horizon.setGuardiao(new Guardian(3));

        horizon.getEntities().get(0).setGold(100);
        horizon.getEntities().get(0).setX(0);

        horizon.getEntities().get(1).setGold(200);
        horizon.getEntities().get(1).setX(50);

        simulation.runIteration(horizon);

        assertEquals(1, horizon.getEntities().size());
        assertTrue(horizon.getEntities().get(0) instanceof CreatureCluster);
        assertEquals(300, horizon.getEntities().get(0).getGold());
    }

    @Test
    @DisplayName("runIteration: Deve fazer o guardião eliminar um cluster na mesma posição")
    void runIteration_shouldMakeGuardianEliminateCluster() {
        simulation = new Simulation(new DeterministicRandomPort(0.0));

        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));

        horizon.getEntities().clear();

        CreatureCluster cluster = new CreatureCluster(10, 500.0, 2000.0);
        horizon.addEntity(cluster);

        horizon.getGuardiao().setX(500.0);
        horizon.getGuardiao().setGold(100.0);

        simulation.runIteration(horizon);

        assertEquals(0, horizon.getEntities().size(), "A lista de entidades deveria estar vazia.");
        assertFalse(horizon.getEntities().contains(cluster), "O cluster não deveria mais existir.");
        assertEquals(2100.0, horizon.getGuardiao().getGold(), "O guardião deveria ter absorvido o ouro do cluster.");
    }

    @Test
    @DisplayName("runIteration: Deve fazer o sobrevivente de uma colisão roubar do vizinho")
    void runIteration_shouldMakeCollisionSurvivorStealFromNeighbor() {
        simulation = new Simulation(new DeterministicRandomPort(0.0));
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.setGuardiao(new Guardian(4));

        horizon.getEntities().get(0).setX(100);
        horizon.getEntities().get(0).setGold(100);
        horizon.getEntities().get(1).setX(100);
        horizon.getEntities().get(1).setGold(100);
        horizon.getEntities().get(2).setX(25201);
        horizon.getEntities().get(2).setGold(1000);

        simulation.runIteration(horizon);

        assertEquals(2, horizon.getEntities().size());

        HorizonEntities clusterSobrevivente = horizon.getEntities().stream()
                .filter(e -> e instanceof CreatureCluster)
                .findFirst()
                .orElseThrow(() -> new AssertionError("O cluster sobrevivente não foi encontrado."));

        HorizonEntities vizinho = horizon.getEntities().stream()
                .filter(e -> e instanceof CreatureUnit)
                .findFirst()
                .orElseThrow(() -> new AssertionError("O vizinho (CreatureUnit) não foi encontrado."));

        assertEquals(350.0, clusterSobrevivente.getGold());
        assertEquals(850.0, vizinho.getGold());
    }

    @Test
    @DisplayName("runIteration: Deve lançar IllegalStateException se a simulação já terminou")
    void runIteration_shouldThrowException_ifSimulationAlreadyEnded() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));
        horizon.setStatus(SimulationStatus.SUCCESSFUL);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            simulation.runIteration(horizon);
        });

        assertEquals("A simulação não pode ser executada pois seu status é: " + horizon.getStatus(),
                exception.getMessage());
    }
}
