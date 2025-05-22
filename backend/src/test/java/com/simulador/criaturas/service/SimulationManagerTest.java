package com.simulador.criaturas.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.simulador.criaturas.dtos.IterationStatusDTO;
import com.simulador.criaturas.exception.InsufficientCreatures;

class SimulationManagerTest {

    private SimulationService simulationService;
    private SimulationManager simulationManager;

    @BeforeEach
    void setUp() {
        simulationService = mock(SimulationService.class);
        simulationManager = new SimulationManager(simulationService);
    }

    // Testes do construtor
    @Test
    void constructor_WithValidSimulationService_DoesNotThrowException() {
        assertThatNoException()
                .isThrownBy(() -> new SimulationManager(simulationService));
    }

    @Test
    void constructor_WithNullSimulationService_ThrowsNullPointerException() {
        assertThatThrownBy(() -> new SimulationManager(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("SimulationService não pode ser null");
    }

    // Testes do método iniciarSimulacao()
    @Nested
    class IniciarSimulacaoTest {

        @Test
        void iniciarSimulacao_WithValidCreatureAmount_ReturnsIterationStatus() {
            IterationStatusDTO status = new IterationStatusDTO();
            when(simulationService.startSimulation(5)).thenReturn(status);

            IterationStatusDTO resultado = simulationManager.iniciarSimulacao(5);

            assertThat(resultado).isEqualTo(status);
            verify(simulationService).startSimulation(5);
        }

        @Test
        void iniciarSimulacao_WithMinimumCreatureAmount_ReturnsIterationStatus() {
            IterationStatusDTO status = new IterationStatusDTO();
            when(simulationService.startSimulation(1)).thenReturn(status);

            IterationStatusDTO resultado = simulationManager.iniciarSimulacao(1);

            assertThat(resultado).isEqualTo(status);
            verify(simulationService).startSimulation(1);
        }

        @Test
        void iniciarSimulacao_WithInvalidCreatureAmount_ThrowsIllegalArgumentException() {
            when(simulationService.startSimulation(0))
                    .thenThrow(new IllegalArgumentException("Quantidade inválida"));

            assertThatThrownBy(() -> simulationManager.iniciarSimulacao(0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Quantidade inválida");

            verify(simulationService).startSimulation(0);
        }

        @Test
        void iniciarSimulacao_WithInsufficientCreatures_ThrowsInsufficientCreaturesException() {
            when(simulationService.startSimulation(100))
                    .thenThrow(new InsufficientCreatures("Não há criaturas suficientes"));

            assertThatThrownBy(() -> simulationManager.iniciarSimulacao(100))
                    .isInstanceOf(InsufficientCreatures.class)
                    .hasMessage("Não há criaturas suficientes");

            verify(simulationService).startSimulation(100);
        }
    }

    // Testes do getter getSimulacao()
    @Test
    void getSimulacao_ReturnsSimulationServiceInstance() {
        assertThat(simulationManager.getSimulacao()).isEqualTo(simulationService);
    }
}
