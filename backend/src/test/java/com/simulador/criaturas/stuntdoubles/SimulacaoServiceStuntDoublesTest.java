package com.simulador.criaturas.stuntdoubles;

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
    void runFullSimulation_executaLoopEterminaComExcecao() {
        // Arrange: Prepara o cenário
        Long userId = 1L;

        Horizon horizonteInicial = new Horizon();
        horizonteInicial.initializeEntities(1);
        horizonteInicial.setGuardiao(new Guardian(2));
        // O status já começa como RUNNING por padrão na inicialização

        when(servicoDeDominio.createNewSimulation(anyInt())).thenReturn(horizonteInicial);

        // MUDANÇA: Simplificamos o mock para que ele lance a exceção na primeira chamada.
        // Isso garante que o loop execute apenas uma vez.
        doThrow(new RuntimeException("Erro de simulação"))
                .when(servicoDeDominio).runIteration(any(Horizon.class));

        // Act & Assert: Executa a ação e verifica o resultado
        assertThrows(RuntimeException.class, () -> simulacaoService.runFullSimulation(1, userId));

        // Assert: Verifica as interações
        // Agora a verificação de 1 chamada vai passar.
        verify(servicoDeDominio, times(1)).runIteration(any(Horizon.class));
        verify(userUseCase, never()).incrementSimulationsRun(userId);
        verify(userUseCase, never()).incrementScore(userId);
    }

    @Test
    @DisplayName("runFullSimulation: [CAMINHO DE EXCEÇÃO] Deve lançar exceção quando userId é nulo")
    void runFullSimulation_caminhoExcecao_quandoUserIdNulo() {
        // Cobre: if (userId == null)
        assertThrows(IllegalArgumentException.class, () -> {
            simulacaoService.runFullSimulation(5, null);
        });
    }

    @Test
    @DisplayName("runFullSimulation: [ESTRUTURAL] Loop não deve executar se o status inicial não for RUNNING")
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
        // Mas a verificação final de stats DEVE ocorrer.
        verify(userUseCase, times(1)).incrementSimulationsRun(userId);
        verify(userUseCase, times(1)).incrementScore(userId);
    }

    @Test
    @DisplayName("runFullSimulation: [ESTRUTURAL] Deve executar loop e terminar com SUCESSO")
    void runFullSimulation_executaLoopEterminaComSucesso() {
        // Cobre o caminho onde o 'while' executa e o 'if' pós-loop é verdadeiro com sucesso.
        // Arrange
        Long userId = 1L;
        Horizon horizonte = new Horizon();
        horizonte.initializeEntities(1);
        horizonte.setGuardiao(new Guardian(2));

        when(servicoDeDominio.createNewSimulation(anyInt())).thenReturn(horizonte);

        // Simula o comportamento do motor: na primeira chamada, continua, na segunda, termina.
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

        // Act
        simulacaoService.runFullSimulation(1, userId);

        // Assert
        verify(servicoDeDominio, times(2)).runIteration(horizonte);
        verify(userUseCase, times(1)).incrementSimulationsRun(userId);
        verify(userUseCase, times(1)).incrementScore(userId);
    }

    @Test
    @DisplayName("runFullSimulation: [ESTRUTURAL] Deve executar loop e terminar com FALHA")
    void runFullSimulation_executaLoopEterminaComFalha() {
        // Cobre o caminho onde o 'while' executa e o 'if' pós-loop é verdadeiro, mas com falha.
        // Arrange
        Long userId = 1L;
        Horizon horizonte = new Horizon();
        horizonte.initializeEntities(1);
        horizonte.setGuardiao(new Guardian(2));
        when(servicoDeDominio.createNewSimulation(anyInt())).thenReturn(horizonte);

        // Simula o comportamento do motor: termina com FALHA na segunda iteração.
        doAnswer(inv -> {
            inv.getArgument(0, Horizon.class).setStatus(SimulationStatus.FAILED);
            return null;
        }).when(servicoDeDominio).runIteration(horizonte);

        // Act
        simulacaoService.runFullSimulation(1, userId);

        // Assert
        verify(servicoDeDominio, atLeastOnce()).runIteration(horizonte);
        verify(userUseCase, times(1)).incrementSimulationsRun(userId);
        // Garante que o score NÃO foi incrementado
        verify(userUseCase, never()).incrementScore(userId);
    }

    @Test
    @DisplayName("runFullSimulation: [ESTRUTURAL] Deve parar por maxIteracoes e NÃO atualizar stats")
    void runFullSimulation_paraPorTimeout() {
        // Cobre o caminho onde o 'while' termina por 'contador < maxIteracoes'
        // e o 'if (horizonte.getStatus() != SimulationStatus.RUNNING)' seguinte é FALSO.

        // Arrange
        Long userId = 1L;
        // Criamos um objeto real que será modificado pelo loop
        Horizon horizonte = new Horizon();
        horizonte.initializeEntities(1);
        horizonte.setGuardiao(new Guardian(2));

        when(servicoDeDominio.createNewSimulation(anyInt())).thenReturn(horizonte);
        // O mock de runIteration não faz nada, mantendo o status como RUNNING para sempre

        // Act
        Horizon resultado = simulacaoService.runFullSimulation(2, userId);

        // Assert
        // O loop rodou até o limite
        verify(servicoDeDominio, times(10000)).runIteration(horizonte);
        // O status final ainda é RUNNING
        assertEquals(SimulationStatus.RUNNING, resultado.getStatus());
        // Como o status não é != RUNNING, os métodos de stats NUNCA são chamados.
        verify(userUseCase, never()).incrementSimulationsRun(userId);
        verify(userUseCase, never()).incrementScore(userId);
    }

}
