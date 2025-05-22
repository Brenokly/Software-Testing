package com.simulador.criaturas.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.simulador.criaturas.dtos.IterationStatusDTO;
import com.simulador.criaturas.exception.InsufficientCreatures;
import com.simulador.criaturas.model.Creature;
import com.simulador.criaturas.model.Creatures;

public class SimulationServiceTest {

    private SimulationService simulationService;

    @Mock
    private Creature currentCreature;

    @Mock
    private Creature neighborCreature;

    @Mock
    private Creatures activeCreatures;

    @Mock
    private Creatures inactiveCreatures;

    @BeforeEach
    public void setup() { // Método que é executado antes de cada teste (Cria o setup para o teste)
        MockitoAnnotations.openMocks(this); // Inicializa os mocks
        simulationService = spy(new SimulationService()); // Cria uma instância do SimulationService com o espionador

        // Configura o comportamento dos mocks
        simulationService.setActiveCreatures(activeCreatures);
        simulationService.setInactiveCreatures(inactiveCreatures);
        simulationService.setIterationCount(1);
        simulationService.setValid(true);
        simulationService.setFinished(false);

        doReturn(currentCreature).when(activeCreatures).getCurrent();
        doReturn(neighborCreature).when(simulationService).findClosestNeighbor(currentCreature);
    }

    // startSimulation()
    // Recebe: int amountOfCreatures
    // Domínio: 2 <= amountOfCreatures <= 10
    // Fronteira: 1, 2, 10, 11
    @Test
    void startSimulation_ValidAmount_ShouldInitializeSimulation() {
        SimulationService simulationServiceT = new SimulationService();
        simulationServiceT.startSimulation(2);

        // Estado inicial
        int currentId = simulationServiceT.getCurrentCreatureId();
        double initialX = simulationServiceT.getActiveCreatures().getCurrent().getX();

        // Executa iteração
        simulationServiceT.iterate();

        // Verifica que a criatura atual moveu
        double afterX = simulationServiceT.getInactiveCreatures().getCreatures().stream()
                .filter(c -> c.getId() == currentId)
                .findFirst()
                .map(Creature::getX)
                .orElseGet(() -> simulationServiceT.getActiveCreatures().getCreatures().stream()
                .filter(c -> c.getId() == currentId)
                .findFirst()
                .map(Creature::getX)
                .orElseThrow());

        assertThat(afterX).isNotEqualTo(initialX);
    }

    @Test
    public void startSimulation_AmountTooLow_ShouldThrowInsufficientCreatures() {
        SimulationService simulationServiceT = new SimulationService();

        assertThatThrownBy(() -> simulationServiceT.startSimulation(1))
                .isInstanceOf(InsufficientCreatures.class)
                .hasMessage("A quantidade de criaturas deve estar entre 2 e 10.");
    }

    @Test
    public void testStartSimulation() {
        SimulationService simulationServiceT = new SimulationService();
        IterationStatusDTO status = simulationServiceT.startSimulation(2);

        assertThat(status).isNotNull();
        assertThat(status.getIterationCount()).isEqualTo(1);
        assertThat(status.getStatusCreatures()).hasSize(2);
        assertThat(status.getInactiveCreatures()).hasSize(0);
        assertThat(status.isFinished()).isFalse();
        assertThat(simulationServiceT.isValid()).isTrue();
    }

    @Test
    public void startSimulation_AmountTooHigh_ShouldThrowInsufficientCreatures() {
        SimulationService simulationServiceT = new SimulationService();
        IterationStatusDTO status = simulationServiceT.startSimulation(10);

        assertThat(status).isNotNull();
        assertThat(status.getIterationCount()).isEqualTo(1);
        assertThat(status.getStatusCreatures()).hasSize(10);
        assertThat(status.getInactiveCreatures()).isEmpty();
        assertThat(status.isFinished()).isFalse();
    }

    @Test
    public void startSimulation_TenCreatures_ShouldInitializeSimulation() {
        SimulationService simulationServiceT = new SimulationService();

        assertThatThrownBy(() -> simulationServiceT.startSimulation(11))
                .isInstanceOf(InsufficientCreatures.class)
                .hasMessage("A quantidade de criaturas deve estar entre 2 e 10.");
    }

    // iterate()
    // Recebe: nenhum parâmetro direto
    // Domínio e partições:
    // isValid = {true, false}
    // isFinished = {true, false}
    // activeCreatures amount = {<2 (invalid), 2..10 (valid), >10 (invalid)}
    // inactiveCreatures amount = [0, +∞]
    // neighborCreature = {null, not null}
    // currentCreature = {null, not null}
    // Caso isValid == false lança exceção, senão retorna status (executa iteração somente se válido)
    @Test
    public void iterate_ValidSimulation_ShouldIncrementIterationAndUpdateStatus() {
        SimulationService simulationServiceT = new SimulationService();
        IterationStatusDTO statusInicial = simulationServiceT.startSimulation(10);

        IterationStatusDTO statusIterado = simulationServiceT.iterate();

        assertThat(statusInicial).isNotNull();
        assertThat(statusInicial.getIterationCount()).isEqualTo(1);

        assertThat(statusIterado).isNotNull();
        assertThat(statusIterado.getIterationCount()).isEqualTo(2);
        assertThat(statusIterado.getStatusCreatures()).hasSize(10);
        assertThat(statusIterado.getInactiveCreatures()).isEmpty();
        assertThat(statusIterado.isFinished()).isFalse();
        assertThat(simulationServiceT.isValid()).isTrue();
    }

    @Test
    public void iterate_SimulationNotValid_ShouldThrowIllegalStateException() {
        simulationService.setValid(false);

        assertThatThrownBy(() -> simulationService.iterate())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("A simulação não foi iniciada corretamente.");
    }

    @Test
    public void iterate_SimulationFinished_ShouldReturnFinishedStatusWithoutIncrement() {
        SimulationService simulationServiceT = new SimulationService();
        simulationServiceT.startSimulation(2);

        simulationServiceT.setFinished(true);

        IterationStatusDTO status = simulationServiceT.iterate();

        assertThat(status).isNotNull();
        assertThat(status.getIterationCount()).isEqualTo(1);
        assertThat(status.getStatusCreatures()).hasSize(2);
        assertThat(status.getInactiveCreatures()).isEmpty();
        assertThat(status.isFinished()).isTrue();
    }

    @Test
    public void iterate_OneCreatureInActive_ShouldThrowIllegalStateException() { // 1
        doReturn(1).when(activeCreatures).getAmountOfCreatures();

        assertThatThrownBy(() -> simulationService.iterate())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("A simulação não foi iniciada corretamente.");
    }

    @Test
    public void iterate_TwoCreatures_ShouldReturnValidStatus() { // 2
        SimulationService simulationServiceT = new SimulationService();
        simulationServiceT.startSimulation(2);

        IterationStatusDTO status = simulationServiceT.iterate();

        assertThat(status).isNotNull();
        assertThat(status.getIterationCount()).isEqualTo(2);
        assertThat(status.getStatusCreatures()).hasSize(2);
        assertThat(status.getInactiveCreatures()).isEmpty();
        assertThat(status.isFinished()).isFalse();
        assertThat(simulationServiceT.isValid()).isTrue();
    }

    @Test
    public void iterate_TenCreatures_ShouldReturnValidStatus() { // 10
        SimulationService simulationServiceT = new SimulationService();
        simulationServiceT.startSimulation(10);

        IterationStatusDTO status = simulationServiceT.iterate();

        assertThat(status).isNotNull();
        assertThat(status.getIterationCount()).isEqualTo(2);
        assertThat(status.getStatusCreatures()).hasSize(10);
        assertThat(status.getInactiveCreatures()).isEmpty();
        assertThat(status.isFinished()).isFalse();
        assertThat(simulationServiceT.isValid()).isTrue();
    }

    @Test
    public void iterate_ElevenCreatures_ShouldThrowIllegalStateException() { // 11
        doReturn(11).when(activeCreatures).getAmountOfCreatures();

        assertThatThrownBy(() -> simulationService.iterate())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("A simulação não foi iniciada corretamente.");
    }

    @Test
    public void iterate_CurrentCreatureIsNull_ShouldThrowIllegalStateException() {
        doReturn(10).when(activeCreatures).getAmountOfCreatures();
        doReturn(null).when(activeCreatures).getCurrent();

        assertThatThrownBy(() -> simulationService.iterate())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("A simulação não foi iniciada corretamente.");
    }

    @Test
    public void iterate_NullNeighborCreature_ShouldReturnFinishedStatus() {
        doReturn(10).when(activeCreatures).getAmountOfCreatures();
        doReturn(null).when(simulationService).findClosestNeighbor(currentCreature);
        assertThat(currentCreature).isNotNull();
        simulationService.setValid(true);
        simulationService.setFinished(false);

        IterationStatusDTO status = simulationService.iterate();

        assertThat(status).isNotNull();
        assertThat(status.getIterationCount()).isEqualTo(1);
        assertThat(status.getInactiveCreatures()).isEmpty();
        assertThat(status.isFinished()).isTrue();
        assertThat(simulationService.isValid()).isTrue();
    }

    @Test
    public void iterate_NeighborGoldLessThanOne_ShouldDeactivateNeighbor() {
        SimulationService simulationServiceT = new SimulationService();

        simulationServiceT.startSimulation(10);
        simulationServiceT.getActiveCreatures().getCreature(1).setGold(0);

        IterationStatusDTO status = simulationServiceT.iterate();

        assertThat(status).isNotNull();
        assertThat(status.getIterationCount()).isEqualTo(2);
        assertThat(status.getStatusCreatures()).hasSize(9);
        assertThat(status.getInactiveCreatures()).hasSize(1);
    }

    @Test
    public void iterate_NeighborGoldExactlyOne_ShouldNotDeactivateNeighbor() {
        SimulationService simulationServiceT = new SimulationService();
        simulationServiceT.startSimulation(2);

        // Configura o vizinho com exatamente 1 de ouro
        Creature neighbor = simulationServiceT.getActiveCreatures().getCreature(1);
        neighbor.setGold(1);

        IterationStatusDTO status = simulationServiceT.iterate();

        // Verifica que o vizinho NÃO foi removido (nenhuma criatura inativa)
        assertThat(status.getStatusCreatures()).hasSize(1);
        assertThat(status.getInactiveCreatures()).hasSize(1);
    }

    @Test
    public void iterate_NeighborGoldGreaterThanOne_ShouldNotDeactivateNeighbor() {
        SimulationService simulationServiceT = new SimulationService();
        simulationServiceT.startSimulation(2);

        // Configura o vizinho com ouro maior que 1
        Creature neighbor = simulationServiceT.getActiveCreatures().getCreature(1);
        neighbor.setGold(2);

        IterationStatusDTO status = simulationServiceT.iterate();

        // Verifica que o vizinho NÃO foi removido (nenhuma criatura inativa)
        assertThat(status.getStatusCreatures()).hasSize(2);
        assertThat(status.getInactiveCreatures()).hasSize(0);

        assertThat(neighbor.getGold()).isEqualTo(1);
    }

    @Test
    public void iterate_MoreThanOneCreature_ShouldAdvanceCurrentCreatureId() {
        SimulationService simulationServiceT = new SimulationService();
        simulationServiceT.startSimulation(2);

        Creature current = simulationServiceT.getActiveCreatures().getCurrent();

        IterationStatusDTO status = simulationServiceT.iterate();

        assertThat(status).isNotNull();
        assertThat(status.getIterationCount()).isEqualTo(2);
        assertThat(simulationServiceT.getCurrentCreatureId()).isEqualTo(1); // Verifica se o ID atual foi incrementado
        assertThat(simulationServiceT.getActiveCreatures().getCurrent()).isNotEqualTo(current); // Verifica se a criatura atual foi alterada
    }

    @Test
    public void iterate_MultipleCreatures_UpdatesIterationAndCurrentCreatureId() {
        SimulationService simulationServiceT = new SimulationService();

        IterationStatusDTO status = simulationServiceT.startSimulation(2);

        assertThat(simulationServiceT.getCurrentCreatureId()).isEqualTo(0); // Antes do next() executar

        IterationStatusDTO statusIterado = simulationServiceT.iterate();

        assertThat(status).isNotNull();
        assertThat(status.getIterationCount()).isEqualTo(1);

        assertThat(statusIterado).isNotNull();
        assertThat(statusIterado.getIterationCount()).isEqualTo(2);
        assertThat(simulationServiceT.getCurrentCreatureId()).isEqualTo(1); // Depois do next() executar
    }

    // GetCurrentCreatureId()
    @Test
    public void getCurrentCreatureId_WithValidSimulation_ReturnsCurrentId() {
        SimulationService simulationServiceT = new SimulationService();
        simulationServiceT.startSimulation(10);

        assertThat(simulationServiceT.getCurrentCreatureId()).isEqualTo(0);
    }

    @Test
    public void getCurrentCreatureId_WithInvalidSimulation_ReturnsMinusOne() {
        doReturn(10).when(activeCreatures).getAmountOfCreatures();
        doReturn(null).when(activeCreatures).getCurrent();

        assertThat(simulationService.getCurrentCreatureId()).isEqualTo(-1);
    }

    // resetSimulation
    // Recebe: nada
    @Test
    public void resetSimulation_AfterStart_ResetsSimulationState() {
        SimulationService simulationServiceT = new SimulationService();
        simulationServiceT.startSimulation(10);

        simulationServiceT.resetSimulation();

        assertThat(simulationServiceT.getActiveCreatures()).isNotNull();
        assertThat(simulationServiceT.getInactiveCreatures()).isNotNull();
        assertThat(simulationServiceT.getActiveCreatures().getAmountOfCreatures()).isEqualTo(10);
        assertThat(simulationServiceT.getInactiveCreatures().getAmountOfCreatures()).isEqualTo(0);
        assertThat(simulationServiceT.getIterationCount()).isEqualTo(1);
        assertThat(simulationServiceT.isValid()).isTrue();
        assertThat(simulationServiceT.isFinished()).isFalse();
    }

    @Test
    void resetSimulation_WithFinishedSimulation_ReturnsValidIterationStatus() {
        SimulationService simulationServiceT = new SimulationService();
        simulationServiceT.startSimulation(5);
        simulationServiceT.iterate();

        simulationServiceT.setFinished(true);

        IterationStatusDTO status = simulationServiceT.resetSimulation();

        assertThat(status).isNotNull();
        assertThat(status.getStatusCreatures()).hasSize(5);
        assertThat(status.getInactiveCreatures()).isEmpty();
        assertThat(status.getIterationCount()).isEqualTo(1);
        assertThat(status.isFinished()).isFalse();
    }

    // finishSimulation()
    // Recebe: nada
    @Test
    public void finishSimulation_WithActiveSimulation_SetsFinishedAndReturnsStatus() {
        SimulationService simulationServiceT = new SimulationService();
        simulationServiceT.startSimulation(10);

        IterationStatusDTO status = simulationServiceT.finishSimulation();

        assertThat(status).isNotNull();
        assertThat(status.getIterationCount()).isEqualTo(1);
        assertThat(status.getStatusCreatures()).hasSize(10);
        assertThat(status.getInactiveCreatures()).isEmpty();
        assertThat(status.isFinished()).isTrue();
    }

    // findClosestNeighbor()
    // Recebe: Creature current
    // Domínio: current != null
    // Fronteira: current == null || current != null
    @Test
    public void findClosestNeighbor_WithValidCurrent_ReturnsNeighbor() {
        Creature neighbor = simulationService.findClosestNeighbor(currentCreature);
        assertThat(neighbor).isNotNull();
    }

    @Test
    public void findClosestNeighbor_WithNullCurrent_ReturnsNull() {
        doReturn(null).when(activeCreatures).getCurrent();

        Creature neighbor = simulationService.findClosestNeighbor(null);
        assertThat(neighbor).isNull();
    }

    @Test
    public void findClosestNeighbor_WithActiveCurrentIsNull_ReturnsNull() {
        doReturn(1).when(activeCreatures).getAmountOfCreatures();

        Creature neighbor = simulationService.findClosestNeighbor(null);

        assertThat(neighbor).isNull();
    }

    @Test
    public void findClosestNeighbor_WithOneCreature_ReturnsNull() {
        SimulationService simulationServiceT = new SimulationService();
        Creatures creatures = new Creatures(1);
        simulationServiceT.setActiveCreatures(creatures);

        Creature neighbor = simulationServiceT.findClosestNeighbor(simulationServiceT.getActiveCreatures().getCurrent());

        assertThat(neighbor).isNull();
    }

    @Test
    public void findClosestNeighbor_WithExactlyOneCreatureInList_ReturnsNull() {
        // Cria a criatura atual
        Creature current = new Creature(1, 5.0, 5.0);

        // Prepara a lista com apenas a própria criatura
        List<Creature> creatures = List.of(current);

        // Configura os mocks
        doReturn(1).when(activeCreatures).getAmountOfCreatures();
        doReturn(creatures).when(activeCreatures).getCreatures();

        // Executa
        Creature neighbor = simulationService.findClosestNeighbor(current);

        // Verifica — com apenas uma criatura não há vizinhos
        assertThat(neighbor).isNull();
    }

    @Test
    public void findClosestNeighbor_WithZeroCreatures_ReturnsNull() {
        SimulationService simulationServiceT = new SimulationService();
        Creatures creatures = new Creatures(0);
        simulationServiceT.setActiveCreatures(creatures);

        Creature current = new Creature(1, 0.0, 1000.0);

        Creature neighbor = simulationServiceT.findClosestNeighbor(current);

        assertThat(neighbor).isNull();
    }

    @Test
    public void findClosestNeighbor_WithTwoCreatures_ReturnsNeighbor() {
        SimulationService simulationServiceT = new SimulationService();
        simulationServiceT.startSimulation(2);

        Creature neighbor = simulationServiceT.findClosestNeighbor(currentCreature);

        assertThat(neighbor).isNotNull();
    }

    @Test
    void findClosestNeighbor_WithMultipleCreatures_ReturnsClosestByDistance() {
        // Criatura atual na posição (5.0)
        Creature current = new Creature(1, 5.0, 0.0);

        // Criaturas:
        // A em (-4.0) → distância real 9 (|5 - (-4)|)
        // B em (4.0)  → distância real 1 (|5 - 4|)
        Creature creatureA = new Creature(2, -4.0, 0.0);
        Creature creatureB = new Creature(3, 4.0, 0.0);

        // Mock da lista de criaturas
        doReturn(List.of(current, creatureA, creatureB)).when(activeCreatures).getCreatures();

        // Executa
        Creature neighbor = simulationService.findClosestNeighbor(current);

        // Verifica que a criatura mais próxima é B (id 3)
        assertThat(neighbor).isNotNull();
        assertThat(neighbor.getId()).isEqualTo(3);
    }

    // nextIfValid()
    // Recebe: nada
    @Test
    public void nextIfValid_WithValidSimulation_IncrementsCurrentCreatureId() {
        SimulationService simulationServiceT = new SimulationService();
        simulationServiceT.startSimulation(10);

        int currentId = simulationServiceT.getCurrentCreatureId();

        simulationServiceT.nextIfValid();

        assertThat(simulationServiceT.getCurrentCreatureId()).isEqualTo(currentId + 1);
    }

    @Test
    public void nextIfValid_WithInvalidSimulation_KeepsCurrentCreatureId() {
        doReturn(1).when(activeCreatures).getAmountOfCreatures();

        int currentId = simulationService.getCurrentCreatureId();

        simulationService.nextIfValid();

        assertThat(simulationService.getCurrentCreatureId()).isEqualTo(currentId);
    }

    // getIterationStatus()
    // Recebe: nada
    @Test
    public void getIterationStatus_WithValidSimulation_ReturnsCorrectStatus() {
        SimulationService simulationServiceT = new SimulationService();
        simulationServiceT.startSimulation(10);

        IterationStatusDTO status = simulationServiceT.getIterationStatus();

        assertThat(status).isNotNull();
        assertThat(status.getIterationCount()).isEqualTo(1);
        assertThat(status.getStatusCreatures()).hasSize(10);
        assertThat(status.getInactiveCreatures()).isEmpty();
        assertThat(status.isFinished()).isFalse();
    }

    @Test
    public void getIterationStatus_WithInvalidSimulation_ThrowsIllegalStateException() {
        SimulationService simulationServiceT = new SimulationService();
        simulationServiceT.startSimulation(10);
        simulationServiceT.setValid(false);

        assertThatThrownBy(() -> simulationServiceT.getIterationStatus())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("A simulação não foi iniciada corretamente.");
    }

    // generateRandomFactor()
    // Recebe: nada
    // Domínio: -1 <= randomFactor <= 1
    // Fronteira: -1.1, -1, 0, 1, 1.1
    @Test
    public void generateRandomFactor_WithinValidRange_ReturnsValueBetweenMinusOneAndOne() {
        double randomFactor = simulationService.generateRandomFactor();

        assertThat(randomFactor).isBetween(-1.0, 1.0);
    }

    @Test
    public void generateRandomFactor_WithFixedRandom_ReturnsExpectedCalculatedValue() {
        Random fixedRandom = new Random() {
            @Override
            public double nextDouble() {
                return 0.75;
            }
        };
        SimulationService service = new SimulationService(fixedRandom);

        double result = service.generateRandomFactor();

        assertThat(result).isEqualTo(0.5);
    }

    // CheckIfTheSimulationIsValid()
    // Recebe: nada
    // Domínio: isValid = {true, false}
    // Partições: 1, 2, 10, 11
    @Test
    public void checkIfTheSimulationIsValid_WithOneCreature_ReturnsFalse() {
        doReturn(1).when(activeCreatures).getAmountOfCreatures();

        assertThat(simulationService.CheckIfTheSimulationIsValid()).isFalse();
    }

    @Test
    public void checkIfTheSimulationIsValid_WithTwoCreatures_ReturnsTrue() {
        doReturn(2).when(activeCreatures).getAmountOfCreatures();

        assertThat(simulationService.CheckIfTheSimulationIsValid()).isTrue();
    }

    @Test
    public void checkIfTheSimulationIsValid_WithTenCreatures_ReturnsTrue() {
        doReturn(10).when(activeCreatures).getAmountOfCreatures();

        assertThat(simulationService.CheckIfTheSimulationIsValid()).isTrue();
    }

    @Test
    public void checkIfTheSimulationIsValid_WithElevenCreatures_ReturnsFalse() {
        doReturn(11).when(activeCreatures).getAmountOfCreatures();

        assertThat(simulationService.CheckIfTheSimulationIsValid()).isFalse();
    }
}
