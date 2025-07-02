package com.simulador.criaturas.stuntdoubles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    @Test
    @DisplayName("getGlobalStatistics: Deve retornar dados paginados e estatísticas globais corretamente")
    void getGlobalStatistics_caminhoPrincipal_comDadosValidos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 5); // Simula a requisição da página 0 com 5 itens
        User userA = new User(1L, "userA", "pass", 1, 10, 20);
        User userB = new User(2L, "userB", "pass", 1, 3, 5);
        List<User> userList = List.of(userA, userB);

        // Cria um objeto Page para simular o retorno do repositório
        Page<User> userPage = new PageImpl<>(userList, pageable, userList.size());

        // "Treina" os mocks
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userRepository.countTotalSimulations()).thenReturn(25L);
        when(userRepository.countTotalSuccesses()).thenReturn(13L);

        // Act
        GlobalStatisticsDTO result = statisticsService.getGlobalStatistics(pageable);

        // Assert
        assertNotNull(result);

        // Verifica as estatísticas globais
        assertEquals(25, result.getTotalSimulationsRun());
        assertEquals(0.52, result.getOverallSuccessRate());

        // Verifica os dados de paginação
        assertEquals(0, result.getCurrentPage());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getTotalUsers());

        // Verifica o conteúdo da página
        assertEquals(2, result.getUserRankingPage().size());
        assertEquals("userA", result.getUserRankingPage().get(0).getLogin());
    }

    @Test
    @DisplayName("getGlobalStatistics: Deve retornar estado vazio quando não há usuários")
    void getGlobalStatistics_caminhoDeBorda_quandoNaoHaUsuarios() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 5);
        Page<User> emptyPage = Page.empty(pageable);

        when(userRepository.findAll(pageable)).thenReturn(emptyPage);
        when(userRepository.countTotalSimulations()).thenReturn(0L);
        when(userRepository.countTotalSuccesses()).thenReturn(0L);

        // Act
        GlobalStatisticsDTO result = statisticsService.getGlobalStatistics(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalSimulationsRun());
        assertEquals(0.0, result.getOverallSuccessRate());
        assertTrue(result.getUserRankingPage().isEmpty());
        assertEquals(0, result.getTotalPages());
    }

    @Test
    @DisplayName("getGlobalStatistics: Deve calcular taxa de sucesso como 0 quando não há simulações")
    void getGlobalStatistics_caminhoDeBorda_comDivisaoPorZero() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 5);
        User userA = new User(1L, "userA", "pass", 1, 0, 0);
        Page<User> userPage = new PageImpl<>(List.of(userA), pageable, 1);

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userRepository.countTotalSimulations()).thenReturn(0L);
        when(userRepository.countTotalSuccesses()).thenReturn(0L);

        // Act
        GlobalStatisticsDTO result = statisticsService.getGlobalStatistics(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0.0, result.getOverallSuccessRate(), "A taxa geral deveria ser 0.");

        assertEquals(1, result.getUserRankingPage().size());
        UserStatisticsDTO statsA = result.getUserRankingPage().get(0);
        assertEquals(0.0, statsA.getSuccessRate(), "A taxa do usuário deveria ser 0.");
    }
}
