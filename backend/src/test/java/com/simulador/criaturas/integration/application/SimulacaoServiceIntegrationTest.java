package com.simulador.criaturas.integration.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.simulador.criaturas.application.SimulacaoService;
import com.simulador.criaturas.domain.model.Horizon;
import com.simulador.criaturas.domain.model.User;
import com.simulador.criaturas.domain.port.out.UserRepositoryPort;
import com.simulador.criaturas.infrastructure.adapter.out.persistence.repository.SpringDataUserRepository;
import com.simulador.criaturas.utils.SimulationStatus;

@SpringBootTest
@Transactional
class SimulacaoServiceIntegrationTest {

    @Autowired
    private SimulacaoService simulacaoService;

    @Autowired
    private UserRepositoryPort userRepositoryPort;

    @Autowired
    private SpringDataUserRepository springDataUserRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        springDataUserRepository.deleteAll();
        testUser = userRepositoryPort.save(new User(null, "sim_tester", "pass", 1, 0, 0));
    }

    @Test
    @DisplayName("runFullSimulation: Deve incrementar as simulações executadas do usuário após o término")
    void runFullSimulation_shouldIncrementSimulationsRun_afterSimulationEnds() {
        int initialSimulationsRun = testUser.getSimulationsRun();

        simulacaoService.runFullSimulation(5, testUser.getId());

        User userFromDb = userRepositoryPort.findById(testUser.getId()).orElseThrow();
        assertThat(userFromDb.getSimulationsRun()).isEqualTo(initialSimulationsRun + 1);
    }

    @Test
    @DisplayName("runFullSimulation: Deve incrementar a pontuação se a simulação for bem-sucedida")
    void runFullSimulation_shouldIncrementScore_whenSimulationIsSuccessful() {
        int initialScore = testUser.getPontuation();
        int initialSimulationsRun = testUser.getSimulationsRun();

        Horizon finalState = simulacaoService.runFullSimulation(2, testUser.getId());

        User userFromDb = userRepositoryPort.findById(testUser.getId()).orElseThrow();

        assertThat(userFromDb.getSimulationsRun()).isEqualTo(initialSimulationsRun + 1);
        if (finalState.getStatus() == SimulationStatus.SUCCESSFUL) {
            assertThat(userFromDb.getPontuation()).isEqualTo(initialScore + 1);
        } else {
            assertThat(userFromDb.getPontuation()).isEqualTo(initialScore);
        }
    }

    @Test
    @DisplayName("runFullSimulation: Deve lançar exceção se o ID do usuário for inválido")
    void runFullSimulation_shouldThrowException_whenUserIdIsInvalid() {
        Long invalidUserId = 9999L;

        assertThatThrownBy(() -> simulacaoService.runFullSimulation(5, invalidUserId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Usuário com ID " + invalidUserId + " não encontrado.");
    }

    @Test
    @DisplayName("runNextSimulation: Deve atualizar estatísticas somente se a simulação terminar")
    void runNextSimulation_shouldOnlyUpdateStats_whenSimulationHasEnded() {
        int initialScore = testUser.getPontuation();
        int initialSimulationsRun = testUser.getSimulationsRun();
        Horizon horizonState = simulacaoService.initNewSimulation(10);

        simulacaoService.runNextSimulation(horizonState, testUser.getId());

        User userFromDb = userRepositoryPort.findById(testUser.getId()).orElseThrow();

        if (horizonState.getStatus() == SimulationStatus.RUNNING) {
            assertThat(userFromDb.getPontuation()).isEqualTo(initialScore);
            assertThat(userFromDb.getSimulationsRun()).isEqualTo(initialSimulationsRun);
        } else {
            assertThat(userFromDb.getSimulationsRun()).isEqualTo(initialSimulationsRun + 1);

            if (horizonState.getStatus() == SimulationStatus.SUCCESSFUL) {
                assertThat(userFromDb.getPontuation()).isEqualTo(initialScore + 1);
            }
        }
    }

    @Test
    @DisplayName("initNewSimulation: Deve retornar um Horizon inicializado corretamente")
    void initNewSimulation_shouldReturnInitializedHorizon() {
        int numberOfCreatures = 7;

        Horizon horizon = simulacaoService.initNewSimulation(numberOfCreatures);

        assertThat(horizon).isNotNull();
        assertThat(horizon.getStatus()).isEqualTo(SimulationStatus.RUNNING);
        assertThat(horizon.getEntities()).hasSize(numberOfCreatures);
        assertThat(horizon.getGuardiao()).isNotNull();
    }
}
