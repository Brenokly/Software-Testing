package com.simulador.criaturas.stuntdoubles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.simulador.criaturas.application.StatisticsService;
import com.simulador.criaturas.domain.model.User;
import com.simulador.criaturas.domain.port.out.UserRepositoryPort;
import com.simulador.criaturas.infrastructure.adapter.in.rest.dto.GlobalStatisticsDTO;
import com.simulador.criaturas.infrastructure.adapter.in.rest.dto.UserStatisticsDTO;

@ExtendWith(MockitoExtension.class)
public class StatisticsServiceStuntDoublesTest {

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    // --- MÉTODO getGlobalStatistics ---
    @Test
    void getGlobalStatistics_caminhoPrincipal_comDadosValidos() {
        // Este teste cobre o 'else' de 'allUsers.isEmpty()' e os 'else's dos
        // operadores ternários, testando o fluxo de cálculo principal.

        // Arrange
        User userA = new User(1L, "userA", "pass", 1, 10, 20); // Taxa de sucesso: 0.5
        User userB = new User(2L, "userB", "pass", 1, 3, 5);  // Taxa de sucesso: 0.6

        // "Treina" o mock para retornar uma lista com estes dois usuários.
        when(userRepository.findAll()).thenReturn(List.of(userA, userB));

        // Act
        GlobalStatisticsDTO result = statisticsService.getGlobalStatistics();

        // Assert
        assertNotNull(result);

        // Verifica as estatísticas globais
        // Total de simulações = 20 (userA) + 5 (userB) = 25
        assertEquals(25, result.getTotalSimulationsRun());
        // Total de sucessos = 10 + 3 = 13. Taxa = 13 / 25 = 0.52
        assertEquals(0.52, result.getOverallSuccessRate());

        // Verifica a lista de estatísticas individuais
        assertNotNull(result.getUserStatistics());
        assertEquals(2, result.getUserStatistics().size());

        // Verifica os dados do userA
        UserStatisticsDTO statsA = result.getUserStatistics().stream().filter(u -> u.getLogin().equals("userA")).findFirst().get();
        assertEquals(10, statsA.getScore());
        assertEquals(20, statsA.getSimulationsRun());
        assertEquals(0.5, statsA.getSuccessRate());
    }

    @Test
    void getGlobalStatistics_caminhoDeBorda_quandoNaoHaUsuarios() {
        // Este teste cobre o 'if (allUsers.isEmpty())'

        // Arrange
        // "Treina" o mock para retornar uma lista vazia.
        when(userRepository.findAll()).thenReturn(List.of());

        // Act
        GlobalStatisticsDTO result = statisticsService.getGlobalStatistics();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalSimulationsRun());
        assertEquals(0.0, result.getOverallSuccessRate());
        assertTrue(result.getUserStatistics().isEmpty());
    }

    @Test
    void getGlobalStatistics_caminhoDeBorda_comDivisaoPorZero() {
        // Este teste cobre o 'true' dos operadores ternários, tanto para o usuário quanto para o global.

        // Arrange
        // Usuário existe mas nunca jogou
        User userA = new User(1L, "userA", "pass", 1, 0, 0);
        when(userRepository.findAll()).thenReturn(List.of(userA));

        // Act
        GlobalStatisticsDTO result = statisticsService.getGlobalStatistics();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalSimulationsRun());
        assertEquals(0.0, result.getOverallSuccessRate(), "A taxa geral deveria ser 0 para evitar divisão por zero.");

        assertEquals(1, result.getUserStatistics().size());
        UserStatisticsDTO statsA = result.getUserStatistics().get(0);
        assertEquals(0.0, statsA.getSuccessRate(), "A taxa do usuário deveria ser 0 para evitar divisão por zero.");
    }
}
