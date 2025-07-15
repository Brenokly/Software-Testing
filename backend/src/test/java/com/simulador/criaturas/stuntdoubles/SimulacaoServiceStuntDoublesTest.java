package com.simulador.criaturas.stuntdoubles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import com.simulador.criaturas.application.SimulacaoService;
import com.simulador.criaturas.domain.model.Guardian;
import com.simulador.criaturas.domain.model.Horizon;
import com.simulador.criaturas.domain.port.in.UserUseCase;
import com.simulador.criaturas.domain.service.Simulation;
import com.simulador.criaturas.utils.SimulationStatus;

@ExtendWith(MockitoExtension.class)
class SimulacaoServiceStuntDoublesTest {

    @Mock
    private Simulation servicoDeDominio;

    @Mock
    private UserUseCase userUseCase;

    @InjectMocks
    private SimulacaoService simulacaoService;

    @Test
    void initNewSimulation_shouldDelegateCreation() {
        Horizon horizonEsperado = new Horizon();
        horizonEsperado.initializeEntities(5);
        horizonEsperado.setGuardiao(new Guardian(6));
        when(servicoDeDominio.createNewSimulation(5)).thenReturn(horizonEsperado);
        Horizon resultado = simulacaoService.initNewSimulation(5);
        assertEquals(horizonEsperado, resultado);
        verify(servicoDeDominio).createNewSimulation(5);
    }

    @Test
    void runNextSimulation_shouldNotUpdateStats_whenSimulationContinues() {
        Long userId = 1L;
        Horizon horizonteEntrada = new Horizon();
        horizonteEntrada.initializeEntities(2);
        horizonteEntrada.setGuardiao(new Guardian(3));
        when(servicoDeDominio.runIteration(horizonteEntrada)).thenReturn(horizonteEntrada);

        simulacaoService.runNextSimulation(horizonteEntrada, userId);

        verify(userUseCase, never()).incrementSimulationsRun(anyLong());
        verify(userUseCase, never()).incrementScore(anyLong());
    }

    @Test
    void runNextSimulation_shouldUpdateStats_whenSimulationEndsSuccessfully() {
        Long userId = 1L;
        Horizon horizonteEntrada = new Horizon();
        horizonteEntrada.initializeEntities(2);
        horizonteEntrada.setGuardiao(new Guardian(3));
        Horizon horizonteSaida = new Horizon();
        horizonteSaida.initializeEntities(2);
        horizonteSaida.setGuardiao(new Guardian(3));
        horizonteSaida.setStatus(SimulationStatus.SUCCESSFUL);
        when(servicoDeDominio.runIteration(horizonteEntrada)).thenReturn(horizonteSaida);

        simulacaoService.runNextSimulation(horizonteEntrada, userId);

        verify(userUseCase, times(1)).incrementSimulationsRun(userId);
        verify(userUseCase, times(1)).incrementScore(userId);
    }

    @Test
    void runNextSimulation_shouldUpdateStats_whenSimulationFails() {
        Long userId = 1L;
        Horizon horizonteEntrada = new Horizon();
        horizonteEntrada.initializeEntities(2);
        horizonteEntrada.setGuardiao(new Guardian(3));
        Horizon horizonteSaida = new Horizon();
        horizonteSaida.initializeEntities(2);
        horizonteSaida.setGuardiao(new Guardian(3));
        horizonteSaida.setStatus(SimulationStatus.FAILED);
        when(servicoDeDominio.runIteration(horizonteEntrada)).thenReturn(horizonteSaida);

        simulacaoService.runNextSimulation(horizonteEntrada, userId);

        verify(userUseCase, times(1)).incrementSimulationsRun(userId);
        verify(userUseCase, never()).incrementScore(userId);
    }

    @Test
    void runNextSimulation_shouldThrowException_whenUserIdIsNull() {
        Horizon horizonteEntrada = new Horizon();
        horizonteEntrada.initializeEntities(2);
        horizonteEntrada.setGuardiao(new Guardian(3));
        assertThrows(IllegalArgumentException.class, () -> simulacaoService.runNextSimulation(horizonteEntrada, null));
    }

    @Test
    void runFullSimulation_shouldThrowException_whenUserIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> simulacaoService.runFullSimulation(5, null));
    }

    @Test
    void runFullSimulation_shouldStopLoopAndThrow_whenIterationFails() {
        Long userId = 1L;
        Horizon horizonteInicial = new Horizon();
        horizonteInicial.initializeEntities(1);
        horizonteInicial.setGuardiao(new Guardian(2));
        when(servicoDeDominio.createNewSimulation(anyInt())).thenReturn(horizonteInicial);
        doThrow(new RuntimeException("Erro de simulação")).when(servicoDeDominio).runIteration(any(Horizon.class));

        assertThrows(RuntimeException.class, () -> simulacaoService.runFullSimulation(1, userId));

        verify(servicoDeDominio, times(1)).runIteration(any(Horizon.class));
        verify(userUseCase, never()).incrementSimulationsRun(userId);
        verify(userUseCase, never()).incrementScore(userId);
    }

    @Test
    @DisplayName("runFullSimulation: [CAMINHO DE EXCEÇÃO] Deve lançar exceção quando userId é nulo")
    void runFullSimulation_exceptionPath_whenUserIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            simulacaoService.runFullSimulation(5, null);
        });
    }

    @Test
    @DisplayName("runFullSimulation: [ESTRUTURAL] Loop não deve executar se o status inicial não for RUNNING")
    void runFullSimulation_shouldNotExecuteLoop_ifStatusIsNotRunning() {
        Long userId = 1L;
        Horizon horizonte = new Horizon();
        horizonte.initializeEntities(1);
        horizonte.setGuardiao(new Guardian(2));
        horizonte.setStatus(SimulationStatus.SUCCESSFUL);
        when(servicoDeDominio.createNewSimulation(anyInt())).thenReturn(horizonte);

        simulacaoService.runFullSimulation(1, userId);

        verify(servicoDeDominio, never()).runIteration(any(Horizon.class));
        verify(userUseCase, times(1)).incrementSimulationsRun(userId);
        verify(userUseCase, times(1)).incrementScore(userId);
    }

    @Test
    @DisplayName("runFullSimulation: [ESTRUTURAL] Deve executar loop e terminar com SUCESSO")
    void runFullSimulation_shouldExecuteLoopAndEndSuccessfully() {
        Long userId = 1L;
        Horizon horizonte = new Horizon();
        horizonte.initializeEntities(1);
        horizonte.setGuardiao(new Guardian(2));
        when(servicoDeDominio.createNewSimulation(anyInt())).thenReturn(horizonte);

        doAnswer(new Answer<Void>() {
            private int count = 0;

            @Override
            public Void answer(InvocationOnMock invocation) {
                Horizon h = invocation.getArgument(0);
                if (count == 0) {
                    h.setStatus(SimulationStatus.RUNNING);
                } else {
                    h.setStatus(SimulationStatus.SUCCESSFUL);
                }
                count++;
                return null;
            }
        }).when(servicoDeDominio).runIteration(horizonte);

        simulacaoService.runFullSimulation(1, userId);

        verify(servicoDeDominio, times(2)).runIteration(horizonte);
        verify(userUseCase, times(1)).incrementSimulationsRun(userId);
        verify(userUseCase, times(1)).incrementScore(userId);
    }

    @Test
    @DisplayName("runFullSimulation: [ESTRUTURAL] Deve executar loop e terminar com FALHA")
    void runFullSimulation_shouldExecuteLoopAndEndWithFailure() {
        Long userId = 1L;
        Horizon horizonte = new Horizon();
        horizonte.initializeEntities(1);
        horizonte.setGuardiao(new Guardian(2));
        when(servicoDeDominio.createNewSimulation(anyInt())).thenReturn(horizonte);

        doAnswer(inv -> {
            inv.getArgument(0, Horizon.class).setStatus(SimulationStatus.FAILED);
            return null;
        }).when(servicoDeDominio).runIteration(horizonte);

        simulacaoService.runFullSimulation(1, userId);

        verify(servicoDeDominio, atLeastOnce()).runIteration(horizonte);
        verify(userUseCase, times(1)).incrementSimulationsRun(userId);
        verify(userUseCase, never()).incrementScore(userId);
    }

    @Test
    @DisplayName("runFullSimulation: Deve parar por maxIteracoes e ATUALIZAR o contador de simulações")
    void runFullSimulation_shouldStopByTimeoutAndIncrementRunCount() {
        Long userId = 1L;
        Horizon horizonte = new Horizon();
        horizonte.initializeEntities(5);
        horizonte.setGuardiao(new Guardian(6));
        horizonte.setStatus(SimulationStatus.RUNNING);
        when(servicoDeDominio.createNewSimulation(anyInt())).thenReturn(horizonte);
        when(servicoDeDominio.runIteration(any(Horizon.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Horizon resultado = simulacaoService.runFullSimulation(5, userId);

        verify(servicoDeDominio, times(10000)).runIteration(horizonte);
        assertThat(resultado.getStatus()).isEqualTo(SimulationStatus.RUNNING);
        verify(userUseCase, times(1)).incrementSimulationsRun(userId);
        verify(userUseCase, never()).incrementScore(userId);
    }
}
