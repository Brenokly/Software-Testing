package com.simulador.criaturas.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.simulador.criaturas.domain.behaviors.HorizonEntities;
import com.simulador.criaturas.domain.model.CreatureCluster;
import com.simulador.criaturas.domain.model.CreatureUnit;
import com.simulador.criaturas.domain.model.Guardian;
import com.simulador.criaturas.domain.model.Horizon;
import com.simulador.criaturas.domain.port.out.RandomPort;
import com.simulador.criaturas.utils.SimulationStatus;

@DisplayName("Testes de Domínio/Fronteira para a classe Simulation (Serviço de Domínio)")
class SimulationTestDF {

    private Simulation simulation;
    private RandomPort deterministicRandomPort;

    @BeforeEach
    public void setUp() {
        deterministicRandomPort = new DeterministicRandomPort(0.1);
        simulation = new Simulation(deterministicRandomPort);
    }

    // --- Dublê de Teste (Stub) ---
    // Uma implementação de RandomPort que retorna valores previsíveis para os testes.
    private static class DeterministicRandomPort implements RandomPort {

        private final double factorToReturn;

        public DeterministicRandomPort(double factor) {
            this.factorToReturn = factor;
        }

        @Override
        public double nextFactor() {
            return factorToReturn;
        }
    }

    // --- MÉTODO getStatus ---
    @Test
    @DisplayName("getStatus: Deve retornar SUCCESSFUL quando só resta o guardião")
    void getStatusDeveRetornarSuccessfulQuandoSoRestaGuardiao() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));

        horizon.getEntities().clear(); // Remove todas as criaturas

        SimulationStatus status = simulation.getStatus(horizon);

        assertEquals(SimulationStatus.SUCCESSFUL, status);
    }

    @Test
    @DisplayName("getStatus: Deve retornar SUCCESSFUL quando resta 1 criatura e o guardião tem mais ouro")
    void getStatusDeveRetornarSuccessfulQuandoGuardiaoTemMaisOuro() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));

        horizon.getGuardiao().setGold(1000);
        horizon.getEntities().get(0).setGold(500);

        SimulationStatus status = simulation.getStatus(horizon);

        assertEquals(SimulationStatus.SUCCESSFUL, status);
    }

    @Test
    @DisplayName("getStatus: Deve retornar FAILED quando resta 1 criatura e o guardião tem menos ouro")
    void getStatusDeveRetornarFailedQuandoGuardiaoTemMenosOuro() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));

        horizon.getGuardiao().setGold(500);
        horizon.getEntities().get(0).setGold(1000);

        SimulationStatus status = simulation.getStatus(horizon);

        assertEquals(SimulationStatus.FAILED, status);
    }

    @Test
    @DisplayName("getStatus: Deve retornar RUNNING quando há múltiplas criaturas")
    void getStatusDeveRetornarRunningQuandoHaMultiplasCriaturas() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.setGuardiao(new Guardian(4));

        SimulationStatus status = simulation.getStatus(horizon);

        assertEquals(SimulationStatus.RUNNING, status);
    }

    // --- MÉTODO runIteration (Cenários de Regras de Negócio) ---
    @Test
    @DisplayName("runIteration: Deve fundir duas criaturas que se movem para a mesma posição")
    void runIterationDeveFundirDuasCriaturas() {
        // Arrange
        // Criamos um RandomPort que força o movimento para o mesmo lugar
        simulation = new Simulation(new DeterministicRandomPort(0.5));
        Horizon horizon = new Horizon();
        horizon.initializeEntities(2);
        horizon.setGuardiao(new Guardian(3));

        // Criatura 1: x=0, gold=100. Move para x=50
        horizon.getEntities().get(0).setGold(100);
        horizon.getEntities().get(0).setX(0);
        // Criatura 2: x=50, gold=200. Move para x=150, mas a colisão acontece antes
        horizon.getEntities().get(1).setGold(200);
        horizon.getEntities().get(1).setX(50);

        // Simular o movimento da primeira criatura, que vai para x=50
        simulation.runIteration(horizon);

        assertEquals(1, horizon.getEntities().size(), "Deveria haver apenas 1 entidade (o cluster) após a fusão.");
        assertTrue(horizon.getEntities().get(0) instanceof CreatureCluster, "A entidade restante deveria ser um CreatureCluster.");
        assertEquals(300, horizon.getEntities().get(0).getGold(), "O ouro do cluster deveria ser a soma dos ouros.");
    }

    @Test
    @DisplayName("runIteration: Deve fazer o guardião eliminar um cluster na mesma posição")
    void runIterationDeveFazerGuardiaoEliminarCluster() {
        // Arrange
        simulation = new Simulation(new DeterministicRandomPort(0.0)); // Ninguém se move

        // Crie um horizonte com uma criatura qualquer, apenas para o construtor funcionar
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));

        // AGORA, LIMPE A LISTA para garantir um cenário 100% controlado
        horizon.getEntities().clear();

        // Adicione APENAS o cluster que queremos testar
        CreatureCluster cluster = new CreatureCluster(10, 500.0, 2000.0);
        horizon.addEntity(cluster);

        // Coloca o guardião e o cluster na mesma posição
        horizon.getGuardiao().setX(500.0);
        horizon.getGuardiao().setGold(100.0);

        simulation.runIteration(horizon);

        // Agora esperamos 0 entidades na lista, pois o único cluster foi removido
        assertEquals(0, horizon.getEntities().size(), "A lista de entidades deveria estar vazia.");
        assertFalse(horizon.getEntities().contains(cluster), "O cluster não deveria mais existir.");
        // O ouro agora será o esperado, pois não houve roubo preventivo de outra criatura
        assertEquals(2100.0, horizon.getGuardiao().getGold(), "O guardião deveria ter absorvido o ouro do cluster.");
    }

    @Test
    @DisplayName("runIteration: Deve fazer o sobrevivente de uma colisão roubar do vizinho")
    void runIterationDeveFazerSobreviventeRoubarDoVizinho() {
        simulation = new Simulation(new DeterministicRandomPort(0.0));
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.setGuardiao(new Guardian(4));

        horizon.getEntities().get(0).setX(100);
        horizon.getEntities().get(0).setGold(100);
        horizon.getEntities().get(1).setX(100);
        horizon.getEntities().get(1).setGold(100);
        horizon.getEntities().get(2).setX(200);
        horizon.getEntities().get(2).setGold(1000);

        simulation.runIteration(horizon);

        assertEquals(2, horizon.getEntities().size(), "Deveria haver 2 entidades: o novo cluster e o vizinho.");

        // Encontre as entidades pelo TIPO, não pelo índice, para um teste robusto
        HorizonEntities clusterSobrevivente = horizon.getEntities().stream()
                .filter(e -> e instanceof CreatureCluster)
                .findFirst()
                .orElseThrow(() -> new AssertionError("O cluster sobrevivente não foi encontrado."));

        // A C3 original é a única CreatureUnit que restou.
        HorizonEntities vizinho = horizon.getEntities().stream()
                .filter(e -> e instanceof CreatureUnit)
                .findFirst()
                .orElseThrow(() -> new AssertionError("O vizinho (CreatureUnit) não foi encontrado."));

        // Agora as asserções são robustas e não dependem da ordem da lista
        assertEquals(350.0, clusterSobrevivente.getGold(), "O ouro final do cluster está incorreto.");
        assertEquals(850.0, vizinho.getGold(), "O ouro final do vizinho está incorreto.");
    }

    @Test
    @DisplayName("runIteration: Deve lançar IllegalStateException se a simulação já terminou")
    void runIterationDeveLancarExcecaoSeJaTerminou() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));

        horizon.setStatus(SimulationStatus.SUCCESSFUL);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            simulation.runIteration(horizon);
        });

        assertEquals("A simulação não pode ser executada pois seu status é: " + horizon.getStatus(), exception.getMessage());
    }
}
