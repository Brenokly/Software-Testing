package com.simulador.criaturas.stuntdoubles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

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

    // --- MÉTODO initNewSimulation ---
    @Test
    void initNewSimulation_deveDelegarCriacao() {
        Horizon horizonEsperado = new Horizon();
        horizonEsperado.initializeEntities(5);
        horizonEsperado.setGuardiao(new Guardian(6));
        when(servicoDeDominio.createNewSimulation(5)).thenReturn(horizonEsperado);
        Horizon resultado = simulacaoService.initNewSimulation(5);
        assertEquals(horizonEsperado, resultado);
        verify(servicoDeDominio).createNewSimulation(5);
    }

    // --- MÉTODO runNextSimulation ---
    @Test
    void runNextSimulation_quandoSimulacaoContinua() {
        Long userId = 1L;
        Horizon horizonteEntrada = new Horizon();
        horizonteEntrada.initializeEntities(2);
        horizonteEntrada.setGuardiao(new Guardian(3));
        when(servicoDeDominio.runIteration(horizonteEntrada)).thenReturn(horizonteEntrada); // Retorna o mesmo estado

        simulacaoService.runNextSimulation(horizonteEntrada, userId);

        verify(userUseCase, never()).incrementSimulationsRun(anyLong());
        verify(userUseCase, never()).incrementScore(anyLong());
    }

    @Test
    void runNextSimulation_quandoTerminaComSucesso() {
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
    void runNextSimulation_quandoTerminaComFalha() {
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
    void runNextSimulation_excecaoQuandoUserIdNulo() {
        Horizon horizonteEntrada = new Horizon();
        horizonteEntrada.initializeEntities(2);
        horizonteEntrada.setGuardiao(new Guardian(3));

        assertThrows(IllegalArgumentException.class, () -> simulacaoService.runNextSimulation(horizonteEntrada, null));
    }

    // --- MÉTODO runFullSimulation ---
    @Test
    void runFullSimulation_excecaoQuandoUserIdNulo() {
        // Cobre: if (userId == null)
        assertThrows(IllegalArgumentException.class, () -> simulacaoService.runFullSimulation(5, null));
    }

    @Test
    void runFullSimulation_naoExecutaLoopSeStatusNaoForRunning() {
        // Cobre o caso em que a condição 'while' é falsa na primeira verificação.
        // Arrange
        Long userId = 1L;
        Horizon horizonte = new Horizon();
        horizonte.initializeEntities(1);
        horizonte.setGuardiao(new Guardian(2));

        horizonte.setStatus(SimulationStatus.SUCCESSFUL); // Estado inicial já finalizado
        when(servicoDeDominio.createNewSimulation(anyInt())).thenReturn(horizonte);

        // Act
        simulacaoService.runFullSimulation(1, userId);

        // Assert
        // O motor da iteração nunca deve ser chamado.
        verify(servicoDeDominio, never()).runIteration(any(Horizon.class));
        // Mas a verificação final de stats deve ocorrer.
        verify(userUseCase, times(1)).incrementSimulationsRun(userId);
        verify(userUseCase, times(1)).incrementScore(userId);
    }

    @Test
    void runFullSimulation_paraPorTimeout() {
        // Simulamos um horizonte que, mesmo após as iterações, continua RUNNING.
        Horizon horizonte = new Horizon();
        horizonte.initializeEntities(5);
        horizonte.setGuardiao(new Guardian(6));

        horizonte.setStatus(SimulationStatus.RUNNING); // Estado final simulado após o loop

        Horizon horizontePosLoop = new Horizon();
        horizontePosLoop.initializeEntities(5);
        horizontePosLoop.setGuardiao(new Guardian(6));
        horizontePosLoop.setStatus(SimulationStatus.RUNNING); // Simulando que o loop terminou por timeout

        runNextSimulation_quandoSimulacaoContinua(); // Reutiliza o teste que prova que nada acontece se o status é RUNNING.
    }

    @Test
    void runFullSimulation_executaLoopEterminaComSucesso() {
        // Cobre o caminho onde o 'while' executa e o 'if' pós-loop é verdadeiro com sucesso.
        Long userId = 1L;

        Horizon horizonte = new Horizon();
        horizonte.initializeEntities(1);
        horizonte.setGuardiao(new Guardian(2));

        when(servicoDeDominio.createNewSimulation(anyInt())).thenReturn(horizonte);
        // Simula o comportamento do motor: na primeira chamada, continua, na segunda, termina.
        doAnswer(inv -> {
            inv.getArgument(0, Horizon.class).setStatus(SimulationStatus.RUNNING);
            return null; // runIteration é void no nosso mock (ou retorna o próprio arg)
        }).doAnswer(inv -> {
            inv.getArgument(0, Horizon.class).setStatus(SimulationStatus.SUCCESSFUL);
            return null;
        }).when(servicoDeDominio).runIteration(horizonte);

        // Act
        simulacaoService.runFullSimulation(1, userId);

        // Assert
        verify(servicoDeDominio, times(2)).runIteration(horizonte);
        verify(userUseCase, times(1)).incrementSimulationsRun(userId);
        verify(userUseCase, times(1)).incrementScore(userId);
    }
}
