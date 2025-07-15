package com.simulador.criaturas.integration.application;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.simulador.criaturas.application.StatisticsService;
import com.simulador.criaturas.domain.model.User;
import com.simulador.criaturas.domain.port.out.UserRepositoryPort;
import com.simulador.criaturas.infrastructure.adapter.in.rest.dto.GlobalStatisticsDTO;
import com.simulador.criaturas.infrastructure.adapter.out.persistence.repository.SpringDataUserRepository;

@SpringBootTest
@Transactional
class StatisticsServiceIntegrationTest {

    @Autowired
    private StatisticsService statisticsService;

    // Injetamos a Porta para preparar os dados, respeitando a arquitetura.
    @Autowired
    private UserRepositoryPort userRepositoryPort;

    // Injetamos a implementação concreta do JPA APENAS para a limpeza do teste.
    @Autowired
    private SpringDataUserRepository springDataUserRepository;

    @BeforeEach
    void setUp() {
        springDataUserRepository.deleteAll();
    }

    @Test
    @DisplayName("getGlobalStatistics: Deve retornar estatísticas zeradas quando não há usuários")
    void getGlobalStatistics_shouldReturnZeroedStats_whenNoUsersExist() {
        Pageable pageable = PageRequest.of(0, 10);

        GlobalStatisticsDTO stats = statisticsService.getGlobalStatistics(pageable);

        assertThat(stats).isNotNull();
        assertThat(stats.getTotalSimulationsRun()).isZero();
        assertThat(stats.getOverallSuccessRate()).isZero();
        assertThat(stats.getUserRankingPage()).isEmpty();
        assertThat(stats.getTotalPages()).isZero();
        assertThat(stats.getTotalUsers()).isZero();
    }

    @Test
    @DisplayName("getGlobalStatistics: Deve retornar os totais corretos e a primeira página de usuários")
    void getGlobalStatistics_shouldReturnCorrectTotalsAndFirstPage() {
        // CORRIGIDO: Usamos a porta para salvar objetos de domínio 'User'.
        userRepositoryPort.save(new User(null, "userC", "p", 1, 10, 20));
        userRepositoryPort.save(new User(null, "userA", "p", 1, 5, 10));
        userRepositoryPort.save(new User(null, "userB", "p", 1, 15, 30));

        Pageable pageable = PageRequest.of(0, 2, Sort.by("login"));

        GlobalStatisticsDTO stats = statisticsService.getGlobalStatistics(pageable);

        assertThat(stats.getTotalSimulationsRun()).isEqualTo(60L);
        assertThat(stats.getOverallSuccessRate()).isEqualTo(0.5);
        assertThat(stats.getTotalUsers()).isEqualTo(3L);
        assertThat(stats.getTotalPages()).isEqualTo(2);
        assertThat(stats.getCurrentPage()).isEqualTo(0);

        assertThat(stats.getUserRankingPage()).hasSize(2);
        assertThat(stats.getUserRankingPage())
                .extracting("login")
                .containsExactly("userA", "userB");
    }

    @Test
    @DisplayName("getGlobalStatistics: Deve retornar a última página de usuários, que pode estar incompleta")
    void getGlobalStatistics_shouldReturnLastPartialPage() {
        userRepositoryPort.save(new User(null, "userC", "p", 1, 10, 20));
        userRepositoryPort.save(new User(null, "userA", "p", 1, 5, 10));
        userRepositoryPort.save(new User(null, "userB", "p", 1, 15, 30));

        Pageable pageable = PageRequest.of(1, 2, Sort.by("login"));

        GlobalStatisticsDTO stats = statisticsService.getGlobalStatistics(pageable);

        assertThat(stats.getCurrentPage()).isEqualTo(1);
        assertThat(stats.getUserRankingPage()).hasSize(1);
        assertThat(stats.getUserRankingPage())
                .extracting("login")
                .containsExactly("userC");
    }

    @Test
    @DisplayName("getGlobalStatistics: Deve respeitar a ordenação solicitada na paginação")
    void getGlobalStatistics_shouldRespectSortOrder() {
        userRepositoryPort.save(new User(null, "userA", "p", 1, 5, 10));
        userRepositoryPort.save(new User(null, "userB", "p", 1, 20, 30));
        userRepositoryPort.save(new User(null, "userC", "p", 1, 10, 20));

        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "pontuation"));

        GlobalStatisticsDTO stats = statisticsService.getGlobalStatistics(pageable);

        assertThat(stats.getUserRankingPage()).hasSize(3);
        assertThat(stats.getUserRankingPage())
                .extracting("login")
                .containsExactly("userB", "userC", "userA");
    }
}
