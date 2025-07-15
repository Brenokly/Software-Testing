package com.simulador.criaturas.structural;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.simulador.criaturas.domain.behaviors.HorizonEntities;
import com.simulador.criaturas.domain.behaviors.LoseGold;
import com.simulador.criaturas.domain.model.CreatureCluster;
import com.simulador.criaturas.domain.model.CreatureUnit;
import com.simulador.criaturas.domain.model.Guardian;
import com.simulador.criaturas.domain.model.Horizon;
import com.simulador.criaturas.domain.port.out.RandomPort;
import com.simulador.criaturas.domain.service.Simulation;
import com.simulador.criaturas.utils.SimulationStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Estruturais (Caixa-Branca / MC/DC) para a classe Simulation")
public class SimulationStructuralTest {

    @Mock
    private RandomPort randomPort;

    @InjectMocks
    private Simulation simulation;

    @Test
    @DisplayName("createNewSimulation: Cobre o caminho principal (else) com valor válido")
    void createNewSimulation_shouldFollowHappyPath_withValidAmount() {
        Horizon h = simulation.createNewSimulation(5);
        assertNotNull(h);
        assertEquals(5, h.getEntities().size());
    }

    @Test
    @DisplayName("createNewSimulation: Cobre o caminho de exceção isolando a condição C1 (<=0)")
    void createNewSimulation_shouldFollowExceptionPath_forAmountZeroOrLess() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> simulation.createNewSimulation(0));
        assertEquals("O número de criaturas deve estar entre 1 e 10.", exception.getMessage());
    }

    @Test
    @DisplayName("createNewSimulation: Cobre o caminho de exceção isolando a condição C2 (>10)")
    void createNewSimulation_shouldFollowExceptionPath_forAmountGreaterThanTen() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> simulation.createNewSimulation(11));
        assertEquals("O número de criaturas deve estar entre 1 e 10.", exception.getMessage());
    }

    @Test
    @DisplayName("runIteration: [CAMINHO DE EXCEÇÃO] Deve lançar exceção se o horizonte for nulo")
    void runIteration_shouldThrowException_whenHorizonIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> simulation.runIteration(null));
        assertEquals("Horizon não pode ser nulo.", exception.getMessage());
    }

    @Test
    @DisplayName("runIteration: [CAMINHO DE EXCEÇÃO] Deve lançar exceção se o status não for RUNNING")
    void runIteration_shouldThrowException_whenStatusIsNotRunning() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));
        horizon.setStatus(SimulationStatus.SUCCESSFUL);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> simulation.runIteration(horizon));
        assertEquals("A simulação não pode ser executada pois seu status é: Successful", exception.getMessage());
    }

    @Test
    @DisplayName("runIteration: [CAMINHO LÓGICO] Deve pular entidade já removida do horizonte")
    void runIteration_shouldSkip_whenEntityIsAlreadyRemoved() {
        when(randomPort.nextFactor()).thenReturn(0.5);
        Horizon horizon = new Horizon();
        horizon.initializeEntities(2);
        horizon.setGuardiao(new Guardian(3));
        horizon.getEntities().get(0).setGold(200);
        horizon.getEntities().get(0).setX(0);
        horizon.getEntities().get(1).setX(100);
        assertDoesNotThrow(() -> simulation.runIteration(horizon));
        assertEquals(1, horizon.getEntities().size());
    }

    @Test
    @DisplayName("runIteration: [MC/DC] Cobre o caminho quando 'survivor' é NULO")
    void runIteration_mcdc_whenSurvivorIsNull() {
        when(randomPort.nextFactor()).thenReturn(0.0);
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(0));
        horizon.getEntities().get(0).setX(999);
        horizon.getEntities().clear();
        assertDoesNotThrow(() -> simulation.runIteration(horizon));
    }

    @Test
    @DisplayName("runIteration: [MC/DC] Cobre o caminho quando 'survivor' é um Guardião")
    void runIteration_mcdc_whenSurvivorIsGuardian() {
        when(randomPort.nextFactor()).thenReturn(0.0);
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));
        horizon.getEntities().clear();
        CreatureCluster cluster = new CreatureCluster(10, 100.0, 1000.0);
        horizon.addEntity(cluster);
        horizon.getGuardiao().setX(100.0);
        simulation.runIteration(horizon);
        assertTrue(horizon.getEntities().isEmpty());
        assertTrue(horizon.getGuardiao().getGold() > 0);
    }

    @Test
    @DisplayName("runIteration: [MC/DC] Cobre o caminho quando 'survivor' não é nulo e não é Guardião")
    void runIteration_mcdc_whenSurvivorIsValidAndNotGuardian() {
        when(randomPort.nextFactor()).thenReturn(0.0);
        Horizon horizon = new Horizon();
        horizon.initializeEntities(2);
        horizon.setGuardiao(new Guardian(3));
        horizon.getEntities().get(0).setX(100);
        horizon.getEntities().get(1).setX(200);
        simulation.runIteration(horizon);
        assertTrue(horizon.getEntities().get(1).getGold() > 0);
    }

    @Test
    @DisplayName("runIteration: Cobre o caminho onde guardião é nulo")
    void runIteration_shouldHandle_whenGuardianIsNull() {
        Horizon mockHorizon = mock(Horizon.class);
        when(mockHorizon.getStatus()).thenReturn(SimulationStatus.RUNNING);
        when(mockHorizon.getEntities()).thenReturn(new ArrayList<>());
        when(mockHorizon.getGuardiao()).thenReturn(null);
        assertDoesNotThrow(() -> simulation.runIteration(mockHorizon));
    }

    @Test
    @DisplayName("runIteration: [ESTRUTURAL] Cobre o caminho onde um item na lista de processamento é nulo")
    void runIteration_shouldHandle_whenEntityInListIsNull() {
        Horizon mockHorizon = mock(Horizon.class);
        List<HorizonEntities> listWithNull = new ArrayList<>();
        listWithNull.add(new CreatureUnit(1));
        listWithNull.add(null);
        when(mockHorizon.getEntities()).thenReturn(listWithNull);
        when(mockHorizon.getStatus()).thenReturn(SimulationStatus.RUNNING);
        assertDoesNotThrow(() -> simulation.runIteration(mockHorizon));
    }

    @Test
    @DisplayName("runIteration: Deve mover o guardião corretamente durante a sua fase de processamento")
    void runIteration_shouldMoveGuardianCorrectly() {
        Horizon horizon = new Horizon();
        Guardian guardian = new Guardian(1, 100.0, 50.0);
        horizon.setGuardiao(guardian);
        double moveFactor = 0.5;
        when(randomPort.nextFactor()).thenReturn(moveFactor);
        double expectedPosition = 100.0 + (moveFactor * 50.0);
        simulation.runIteration(horizon);
        assertEquals(expectedPosition, horizon.getGuardiao().getX());
        verify(randomPort, times(1)).nextFactor();
    }

    private static class NonMovableEntity implements HorizonEntities {

        private int id = 999;
        private double x = 0;
        private double gold = 0;

        @Override
        public int getId() {
            return id;
        }

        @Override
        public double getX() {
            return x;
        }

        @Override
        public void setX(double newX) {
            this.x = newX;
        }

        @Override
        public double getGold() {
            return gold;
        }

        @Override
        public void setGold(double newGold) {
            this.gold = newGold;
        }
    }

    @Test
    @DisplayName("runIteration: [ESTRUTURAL] Cobre o caminho quando a entidade não pode se mover")
    void runIteration_shouldHandle_whenEntityCannotMove() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));
        NonMovableEntity nonMovable = new NonMovableEntity();
        nonMovable.setX(500.0);
        horizon.addEntity(nonMovable);
        double initialPosition = nonMovable.getX();
        simulation.runIteration(horizon);
        assertEquals(initialPosition, nonMovable.getX());
    }

    @Test
    @DisplayName("runIteration: [MC/DC] Cobre o par onde 'survivor' é nulo")
    void runIteration_mcdc_whenSurvivorIsNull_forPairCoverage() {
        when(randomPort.nextFactor()).thenReturn(0.1);
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));
        horizon.getEntities().get(0).setX(0);
        horizon.getEntities().get(0).setGold(0);
        horizon.getEntities().remove(0);
        assertDoesNotThrow(() -> simulation.runIteration(horizon));
    }

    @Test
    @DisplayName("getStatus: [CAMINHO DE EXCEÇÃO] Deve lançar exceção se horizonte for nulo")
    void getStatus_shouldThrowException_whenHorizonIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> simulation.getStatus(null));
        assertEquals("Horizon não pode ser nulo.", exception.getMessage());
    }

    @Test
    @DisplayName("getStatus: [MC/DC] Cobre o caminho FAILED pela condição de IGUALDADE de ouro")
    void getStatus_mcdc_shouldReturnFailed_onGoldEquality() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));
        horizon.getGuardiao().setGold(500);
        horizon.getEntities().get(0).setGold(500);
        SimulationStatus status = simulation.getStatus(horizon);
        assertEquals(SimulationStatus.FAILED, status);
    }

    //---------------------------------------------------------------
    @Test
    @DisplayName("getStatus: [MC/DC] Cobre o caminho FAILED pela condição de ouro maior")
    void getStatus_mcdc_shouldReturnFailed_onHigherCreatureGold() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));
        horizon.getGuardiao().setGold(100);
        horizon.getEntities().get(0).setGold(150);
        assertEquals(SimulationStatus.FAILED, simulation.getStatus(horizon));
    }

    @Test
    @DisplayName("getStatus: [MC/DC] Cobre o caminho FAILED pela condição de ouro igual")
    void getStatus_mcdc_shouldReturnFailed_onEqualGold() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));
        horizon.getGuardiao().setGold(100);
        horizon.getEntities().get(0).setGold(100);
        assertEquals(SimulationStatus.FAILED, simulation.getStatus(horizon));
    }

    @Test
    @DisplayName("getStatus: [MC/DC] Cobre o caminho RUNNING pela condição de tamanho")
    void getStatus_mcdc_shouldReturnRunning_bySizeCondition() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(2);
        horizon.setGuardiao(new Guardian(3));
        assertEquals(SimulationStatus.RUNNING, simulation.getStatus(horizon));
    }

    @Test
    @DisplayName("getStatus: [MC/DC] Cobre o caminho SUCCESSFUL pela condição 'isEmpty'")
    void getStatus_mcdc_shouldReturnSuccessful_byIsEmptyCondition() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));
        horizon.getEntities().clear();
        assertEquals(SimulationStatus.SUCCESSFUL, simulation.getStatus(horizon));
    }

    @Test
    @DisplayName("getStatus: [MC/DC] Cobre o caminho SUCCESSFUL pela condição de ouro")
    void getStatus_mcdc_shouldReturnSuccessful_byGoldCondition() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));
        horizon.getGuardiao().setGold(100);
        horizon.getEntities().get(0).setGold(50);
        assertEquals(SimulationStatus.SUCCESSFUL, simulation.getStatus(horizon));
    }

    @Test
    @DisplayName("getStatus: [MC/DC] Cobre o caminho FAILED")
    void getStatus_shouldReturnFailed() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));
        horizon.getGuardiao().setGold(100);
        horizon.getEntities().get(0).setGold(150);
        assertEquals(SimulationStatus.FAILED, simulation.getStatus(horizon));
    }

    @Test
    @DisplayName("getStatus: Cobre o caminho RUNNING (caminho 'else' final)")
    void getStatus_shouldReturnRunning() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(2);
        horizon.setGuardiao(new Guardian(3));
        assertEquals(SimulationStatus.RUNNING, simulation.getStatus(horizon));
    }

    @Test
    @DisplayName("getStatus: Cobre o caminho RUNNING (caminho 'else' final)")
    void getStatus_shouldReturnRunning_finalPath() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(2);
        horizon.setGuardiao(new Guardian(3));
        assertEquals(SimulationStatus.RUNNING, simulation.getStatus(horizon));
    }

    @Test
    @DisplayName("getStatus: Cobre o caminho FAILED (Decisão 3 sendo VERDADEIRA)")
    void getStatus_mcdc_shouldReturnFailed_forDecision3() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));
        horizon.getGuardiao().setGold(100);
        horizon.getEntities().get(0).setGold(150);
        assertEquals(SimulationStatus.FAILED, simulation.getStatus(horizon));
    }

    @Test
    @DisplayName("getStatus: Cobre o caminho SUCCESSFUL (Decisão 2 sendo VERDADEIRA)")
    void getStatus_mcdc_shouldReturnSuccessful_forDecision2() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));
        horizon.getEntities().clear();
        assertEquals(SimulationStatus.SUCCESSFUL, simulation.getStatus(horizon));
    }

    @Test
    @DisplayName("findNearestNeighbor: Cobre o caminho de exceção onde horizonte é nulo")
    void findNearestNeighbor_shouldThrowException_whenHorizonIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> simulation.findNearestNeighbor(null, new CreatureUnit(1)));
        assertEquals("Horizon e entidade atual não podem ser nulos.", exception.getMessage());
    }

    @Test
    @DisplayName("findNearestNeighbor: Cobre o caminho de exceção onde entidade atual é nula")
    void findNearestNeighbor_shouldThrowException_whenCurrentEntityIsNull() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> simulation.findNearestNeighbor(horizon, null));
        assertEquals("Horizon e entidade atual não podem ser nulos.", exception.getMessage());
    }

    @Test
    @DisplayName("findNearestNeighbor: Cobre o caminho onde não há vizinhos")
    void findNearestNeighbor_shouldReturnNull_whenNoNeighborsExist() {
        when(randomPort.nextFactor()).thenReturn(0.0);
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));
        simulation.runIteration(horizon);
        assertEquals(1000000, horizon.getEntities().get(0).getGold());
    }

    @Test
    @DisplayName("findNearestNeighbor: Cobre o caminho onde há vizinhos")
    void findNearestNeighbor_shouldReturnNeighbor_whenNeighborsExist() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(2);
        horizon.setGuardiao(new Guardian(2));
        horizon.getEntities().get(0).setX(100.0);
        horizon.getEntities().get(1).setX(200.0);
        HorizonEntities neighbor = simulation.findNearestNeighbor(horizon, horizon.getEntities().get(0));
        assertNotNull(neighbor);
        assertEquals(200.0, neighbor.getX());
    }

    @Test
    @DisplayName("findNearestNeighbor: Cobre o caminho onde há vizinhos, mas a entidade atual é nula")
    void findNearestNeighbor_shouldThrowException_whenCurrentEntityIsNullWithNeighbors() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(2);
        horizon.setGuardiao(new Guardian(2));
        horizon.getEntities().get(0).setX(100.0);
        horizon.getEntities().get(1).setX(200.0);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> simulation.findNearestNeighbor(horizon, null));
        assertEquals("Horizon e entidade atual não podem ser nulos.", exception.getMessage());
    }

    @Test
    @DisplayName("treatNeighborTheft: Cobre o caminho onde não há vítima para roubar")
    void treatNeighborTheft_shouldDoNothing_whenVictimIsNull() {
        when(randomPort.nextFactor()).thenReturn(0.0);
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));
        simulation.runIteration(horizon);
        assertEquals(1000000, horizon.getEntities().get(0).getGold());
    }

    @Test
    @DisplayName("treatNeighborTheft: Cobre o caminho onde a vítima não pode perder ouro")
    void treatNeighborTheft_shouldDoNothing_whenVictimCannotLoseGold() {
        when(randomPort.nextFactor()).thenReturn(0.0);
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));
        HorizonEntities fakeVictim = new NonMovableEntity2();
        fakeVictim.setX(200.0);
        fakeVictim.setGold(5000.0);
        horizon.addEntity(fakeVictim);
        double c1InitialGold = horizon.getEntities().get(0).getGold();
        simulation.runIteration(horizon);
        assertEquals(c1InitialGold, horizon.getEntities().get(0).getGold());
    }

    @Test
    @DisplayName("treatNeighborTheft: Cobre o caminho onde a vítima perde ouro")
    void treatNeighborTheft_shouldStealGold_whenVictimCanLoseGold() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(2);
        horizon.setGuardiao(new Guardian(2));
        HorizonEntities victim = new NonMovableEntity2();
        victim.setX(200.0);
        victim.setGold(5000.0);
        horizon.addEntity(victim);
        double c1InitialGold = horizon.getEntities().get(0).getGold();
        simulation.treatNeighborTheft(horizon, horizon.getEntities().get(0));
        assertTrue(horizon.getEntities().get(0).getGold() > c1InitialGold);
    }

    //-------------------------------------------------------
    @Test
    @DisplayName("treatNeighborTheft: Cobre o caminho onde o atacante não pode roubar ouro")
    void treatNeighborTheft_shouldDoNothing_whenAttackerCannotSteal() {
        when(randomPort.nextFactor()).thenReturn(0.0);
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));
        HorizonEntities attacker = new NonMovableEntity2();
        attacker.setX(100.0);
        horizon.addEntity(attacker);
        double c1InitialGold = horizon.getEntities().get(0).getGold();
        simulation.runIteration(horizon);
        assertEquals(c1InitialGold, horizon.getEntities().get(0).getGold());
    }

    @Test
    @DisplayName("resolveInteractionsAt: Cobre o caminho de exceção onde horizonte é nulo")
    void resolveInteractionsAt_shouldThrowException_whenHorizonIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> simulation.resolveInteractionsAt(null, 100.0));
        assertEquals("Horizon não pode ser nulo e a posição deve ser um número válido.", exception.getMessage());
    }

    @Test
    @DisplayName("resolveInteractionsAt: Cobre o caminho de exceção onde posição é NaN")
    void resolveInteractionsAt_shouldThrowException_whenPositionIsNaN() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> simulation.resolveInteractionsAt(horizon, Double.NaN));
        assertEquals("Horizon não pode ser nulo e a posição deve ser um número válido.", exception.getMessage());
    }

    @Test
    @DisplayName("resolveInteractionsAt: Cobre o caminho de exceção onde posição é infinita")
    void resolveInteractionsAt_shouldThrowException_whenPositionIsInfinite() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> simulation.resolveInteractionsAt(horizon, Double.POSITIVE_INFINITY));
        assertEquals("Horizon não pode ser nulo e a posição deve ser um número válido.", exception.getMessage());
    }

    @Test
    @DisplayName("resolveInteractionsAt: Cobre o caminho onde não há colisão (size <= 1)")
    void resolveInteractionsAt_shouldDoNothing_whenNoCollision() {
        when(randomPort.nextFactor()).thenReturn(0.0);
        Horizon horizon = new Horizon();
        horizon.initializeEntities(2);
        horizon.setGuardiao(new Guardian(3));
        horizon.getEntities().get(0).setX(100.0);
        horizon.getEntities().get(1).setX(200.0);
        long initialEntityCount = horizon.getEntities().size();
        simulation.runIteration(horizon);
        assertEquals(initialEntityCount, horizon.getEntities().size());
    }

    @Test
    @DisplayName("resolveInteractionsAt: Cobre o caminho de fusão com cluster pré-existente")
    void resolveInteractionsAt_shouldFuseWithExistingCluster() {
        when(randomPort.nextFactor()).thenReturn(0.0);
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));
        CreatureCluster cluster = new CreatureCluster(10, 100.0, 1000.0);
        horizon.addEntity(cluster);
        horizon.getEntities().get(0).setX(100.0);
        simulation.runIteration(horizon);
        assertEquals(1, horizon.getEntities().size());
        assertTrue(horizon.getEntities().get(0) instanceof CreatureCluster);
        assertTrue(horizon.getEntities().get(0).getGold() > 1000.0);
    }

    private static class NonMovableEntity2 implements HorizonEntities, LoseGold {

        @SuppressWarnings("FieldMayBeFinal")
        private int id = 998;
        private double x = 0;
        private double gold = 0;

        @Override
        public int getId() {
            return id;
        }

        @Override
        public double getX() {
            return x;
        }

        @Override
        public void setX(double newX) {
            this.x = newX;
        }

        @Override
        public double getGold() {
            return gold;
        }

        @Override
        public void setGold(double newGold) {
            this.gold = newGold;
        }

        @Override
        public double loseGold(double percentage) {
            return 0;
        }
    }

    @Test
    @DisplayName("resolveInteractionsAt: Cobre o caminho de fusão com cluster novo")
    void resolveInteractionsAt_shouldFuseIntoNewCluster() {
        when(randomPort.nextFactor()).thenReturn(0.0);
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));
        horizon.getEntities().get(0).setX(100.0);
        CreatureCluster newCluster = new CreatureCluster(10, 100.0, 1000.0);
        horizon.addEntity(newCluster);
        simulation.runIteration(horizon);
        assertEquals(1, horizon.getEntities().size());
        assertTrue(horizon.getEntities().get(0) instanceof CreatureCluster);
        assertTrue(horizon.getEntities().get(0).getGold() > 1000.0);
    }

    @Test
    @DisplayName("resolveInteractionsAt: Cobre o caminho onde não há sobrevivente")
    void resolveInteractionsAt_pathWithNoSurvivor() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));
        horizon.getEntities().clear();
        horizon.addEntity(new CreatureCluster(10, 100.0, 1000.0));
        horizon.getGuardiao().setX(100.0);
        HorizonEntities survivor = simulation.resolveInteractionsAt(horizon, 100.0);
        assertTrue(survivor instanceof Guardian, "O sobrevivente deve ser o guardião.");
        assertEquals(horizon.getEntities().size(), 0, "Deveria não haver sobreviventes após a colisão, apenas o guardião.");
    }

    @Test
    @DisplayName("resolveInteractionsAt: Cobre o caminho onde há sobrevivente")
    void resolveInteractionsAt_pathWithSurvivor() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));
        horizon.getEntities().get(0).setX(100.0);
        HorizonEntities survivor = simulation.resolveInteractionsAt(horizon, 100.0);
        assertNotNull(survivor, "Deveria retornar um sobrevivente quando há colisão.");
        assertEquals(100.0, survivor.getX(), "O sobrevivente deveria estar na posição x=100.");
    }

    @Test
    @DisplayName("resolveInteractionsAt: Cobre o caminho onde há sobrevivente e é um Guardião")
    void resolveInteractionsAt_pathWithGuardianAsSurvivor() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));
        horizon.getEntities().get(0).setX(100.0);
        HorizonEntities survivor = simulation.resolveInteractionsAt(horizon, 100.0);
        assertNotNull(survivor, "Deveria retornar um sobrevivente quando há colisão.");
        assertTrue(survivor instanceof CreatureUnit, "O sobrevivente deveria ser uma entidade do tipo CreatureUnit.");
    }

    @Test
    @DisplayName("resolveInteractionsAt: Cobre o caminho onde há sobrevivente e não é um Guardião")
    void resolveInteractionsAt_pathWithNonGuardianSurvivor() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));
        horizon.getEntities().get(0).setX(100.0);
        HorizonEntities survivor = simulation.resolveInteractionsAt(horizon, 100.0);
        assertNotNull(survivor, "Deveria retornar um sobrevivente quando há colisão.");
        assertTrue((survivor instanceof CreatureUnit), "O sobrevivente não deveria ser um Guardião.");
    }

}
