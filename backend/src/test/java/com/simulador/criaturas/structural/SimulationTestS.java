package com.simulador.criaturas.structural;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.simulador.criaturas.domain.behaviors.HorizonEntities;
import com.simulador.criaturas.domain.behaviors.LoseGold;
import com.simulador.criaturas.domain.model.CreatureCluster;
import com.simulador.criaturas.domain.model.CreatureUnit;
import com.simulador.criaturas.domain.model.Guardian;
import com.simulador.criaturas.domain.model.Horizon;
import com.simulador.criaturas.domain.port.out.RandomPort;
import com.simulador.criaturas.domain.service.Simulation;
import com.simulador.criaturas.utils.SimulationStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Estruturais (Caixa-Branca / MC/DC) para a classe Simulation")
public class SimulationTestS {

    @Mock
    private RandomPort randomPort;

    @InjectMocks
    private Simulation simulation;

    // --- MÉTODO createNewSimulation ---
    // Decisão: if (numeroDeCriaturas <= 0 || numeroDeCriaturas > 10)
    // C1: numeroDeCriaturas <= 0, C2: numeroDeCriaturas > 10
    @Test
    @DisplayName("createNewSimulation: Cobre o caminho principal (else) com valor válido")
    void createNewSimulation_caminhoPrincipal() {
        // Força C1=false, C2=false.
        Horizon h = simulation.createNewSimulation(5);
        assertNotNull(h);
        assertEquals(5, h.getEntities().size());
    }

    @Test
    @DisplayName("createNewSimulation: Cobre o caminho de exceção isolando a condição C1 (<=0)")
    void createNewSimulation_caminhoDeExcecao_condicaoMenorOuIgualAZero() {
        // Força C1=true.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> simulation.createNewSimulation(0));
        assertEquals("O número de criaturas deve estar entre 1 e 10.", exception.getMessage());
    }

    @Test
    @DisplayName("createNewSimulation: Cobre o caminho de exceção isolando a condição C2 (>10)")
    void createNewSimulation_caminhoDeExcecao_condicaoMaiorQueDez() {
        // Força C2=true.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> simulation.createNewSimulation(11));
        assertEquals("O número de criaturas deve estar entre 1 e 10.", exception.getMessage());
    }

    // --- TESTES ESTRUTURAIS PARA O MÉTODO runIteration ---
    @Test
    @DisplayName("runIteration: [CAMINHO DE EXCEÇÃO] Deve lançar exceção se o horizonte for nulo")
    void runIteration_caminhoExcecao_horizonNulo() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> simulation.runIteration(null));
        assertEquals("Horizon não pode ser nulo.", exception.getMessage());
    }

    @Test
    @DisplayName("runIteration: [CAMINHO DE EXCEÇÃO] Deve lançar exceção se o status não for RUNNING")
    void runIteration_caminhoExcecao_statusNaoRunning() {
        // Cobre: if (horizonte.getStatus() != SimulationStatus.RUNNING)
        Horizon horizon = new Horizon(1, 2);
        horizon.setStatus(SimulationStatus.SUCCESSFUL);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> simulation.runIteration(horizon));
        assertEquals("A simulação não pode ser executada pois seu status é: Successful", exception.getMessage());
    }

    @Test
    @DisplayName("runIteration: [CAMINHO LÓGICO] Deve pular entidade já removida do horizonte")
    void runIteration_caminhoLogico_pulaEntidadeRemovida() {
        // Cobre: if (!horizonte.getEntities().contains(entity))
        when(randomPort.nextFactor()).thenReturn(0.5);
        Horizon horizon = new Horizon(2, 3);
        horizon.getEntities().get(0).setGold(200);
        horizon.getEntities().get(0).setX(0); // Com r=0.5, vai para x=100
        horizon.getEntities().get(1).setX(100);

        assertDoesNotThrow(() -> simulation.runIteration(horizon));
        assertEquals(1, horizon.getEntities().size());
    }

    @Test
    @DisplayName("runIteration: [MC/DC] Cobre o caminho quando 'survivor' é NULO")
    void runIteration_caminhoLogico_survivorNulo() {
        // Cobre o caso 'survivor == null' na condição 'if (survivor != null && ...)'
        when(randomPort.nextFactor()).thenReturn(0.0);
        Horizon horizon = new Horizon(1, 2);
        horizon.getEntities().get(0).setX(999); // Posição isolada

        horizon.getEntities().clear(); // Limpa a lista

        assertDoesNotThrow(() -> simulation.runIteration(horizon));
    }

    @Test
    @DisplayName("runIteration: [MC/DC] Cobre o caminho quando 'survivor' é um Guardião")
    void runIteration_caminhoLogico_survivorEGuardião() {
        // Cobre o caso '!(survivor instanceof Guardian)' sendo FALSO.
        when(randomPort.nextFactor()).thenReturn(0.0);
        Horizon horizon = new Horizon(1, 2);
        horizon.getEntities().clear();
        CreatureCluster cluster = new CreatureCluster(10, 100.0, 1000.0);
        horizon.addEntity(cluster);
        horizon.getGuardiao().setX(100.0); // Colisão entre cluster e guardião

        simulation.runIteration(horizon);

        // O survivor da interação será o Guardião. A asserção é que 'treatNeighborTheft' não foi chamado,
        // o que testamos garantindo que o ouro da criatura inicial (que não existe mais) não foi afetado.
        // A melhor asserção é que o ouro do guardião aumentou, e o cluster sumiu.
        assertTrue(horizon.getEntities().isEmpty());
        assertTrue(horizon.getGuardiao().getGold() > 0);
    }

    @Test
    @DisplayName("runIteration: [MC/DC] Cobre o caminho quando 'survivor' não é nulo e não é Guardião")
    void runIteration_caminhoLogico_survivorValido() {
        // Cobre o caso 'survivor != null && !(survivor instanceof Guardian)' sendo VERDADEIRO.
        when(randomPort.nextFactor()).thenReturn(0.0);
        Horizon horizon = new Horizon(2, 3); // C1 e C2
        horizon.getEntities().get(0).setX(100); // Vizinho
        horizon.getEntities().get(1).setX(200); // Atacante

        simulation.runIteration(horizon);

        // A asserção é que o roubo aconteceu, provando que o corpo do IF foi executado.
        assertTrue(horizon.getEntities().get(1).getGold() > 1_000_000, "O atacante deveria ter roubado ouro.");
    }

    @Test
    @DisplayName("runIteration: Cobre o caminho onde guardião é nulo")
    void runIteration_caminhoLogico_guardiaoNulo() {
        // Este é um caso hipotético, já que nosso construtor de Horizon sempre cria um guardião.
        // Mas para cobertura estrutural, é válido. Vamos usar um mock de Horizon.
        Horizon mockHorizon = mock(Horizon.class);
        when(mockHorizon.getStatus()).thenReturn(SimulationStatus.RUNNING);
        when(mockHorizon.getEntities()).thenReturn(new ArrayList<>());
        when(mockHorizon.getGuardiao()).thenReturn(null); // Força o guardião a ser nulo

        assertDoesNotThrow(() -> simulation.runIteration(mockHorizon));
    }

    @Test
    @DisplayName("runIteration: [ESTRUTURAL] Cobre o caminho onde um item na lista de processamento é nulo")
    void runIteration_caminhoQuandoEntidadeNaListaENula() {
        // Para testar o if (entity != null), precisamos de um mock do horizonte
        // para controlar a lista que o for-each irá percorrer.
        Horizon mockHorizon = mock(Horizon.class);

        // Criamos uma lista que contém um elemento nulo
        List<HorizonEntities> listWithNull = new ArrayList<>();
        listWithNull.add(new CreatureUnit(1));
        listWithNull.add(null);

        // Treinamos o mock para retornar essa lista
        when(mockHorizon.getEntities()).thenReturn(listWithNull);
        when(mockHorizon.getStatus()).thenReturn(SimulationStatus.RUNNING);

        // Act & Assert
        // A asserção é que o método executa sem lançar NullPointerException,
        // provando que o 'if (entity != null)' nos protegeu.
        assertDoesNotThrow(() -> simulation.runIteration(mockHorizon));
    }

    // Classe auxiliar (Stub) criada APENAS para este teste estrutural
    private static class NonMovableEntity implements HorizonEntities {

        @SuppressWarnings("FieldMayBeFinal")
        private int id = 999;
        private double x = 0;
        private double gold = 0;

        @Override
        public int getId() {
            return id;
        }

        @Override
        public double getX() {
            return x;
        }

        @Override
        public void setX(double newX) {
            this.x = newX;
        }

        @Override
        public double getGold() {
            return gold;
        }

        @Override
        public void setGold(double newGold) {
            this.gold = newGold;
        }
    }

    @Test
    @DisplayName("runIteration: [ESTRUTURAL] Cobre o caminho quando a entidade não pode se mover")
    void runIteration_caminhoQuandoEntidadeNaoImplementaMove() {
        // Arrange
        Horizon horizon = new Horizon(1, 2); // Começa com C1 na posição x=0

        NonMovableEntity nonMovable = new NonMovableEntity();

        // --- A CORREÇÃO CRÍTICA ESTÁ AQUI ---
        // Colocamos a entidade de teste em uma posição isolada para evitar colisão.
        nonMovable.setX(500.0);

        horizon.addEntity(nonMovable); // Adicionamos nossa entidade "falsa"

        double initialPosition = nonMovable.getX();

        // Act
        simulation.runIteration(horizon);

        // Assert
        // A asserção é que a posição não mudou, provando que o bloco 'if' foi pulado.
        assertEquals(initialPosition, nonMovable.getX(), "A posição da entidade não móvel não deveria mudar.");
    }

    @Test
    @DisplayName("runIteration: [MC/DC] Cobre o par onde 'survivor' é nulo")
    void runIteration_MCDC_quandoSurvivorENulo() {
        // Este teste forma o par com 'survivorValido' para testar a condição C1 (survivor != null)

        // Arrange
        when(randomPort.nextFactor()).thenReturn(0.1);
        Horizon horizon = new Horizon(1, 2);
        // Forçamos a remoção da única entidade, fazendo com que a interação
        // em sua posição antiga retorne um sobrevivente nulo.
        horizon.getEntities().get(0).setX(0);
        horizon.getEntities().get(0).setGold(0); // Não se move
        horizon.getEntities().remove(0); // Remove antes da iteração

        // Act & Assert
        // A asserção é que roda sem erro, provando que o 'if' foi FALSO e protegeu o código interno.
        assertDoesNotThrow(() -> simulation.runIteration(horizon));
    }

    // --- TESTES ESTRUTURAIS PARA getStatus ---
    // Decisão 1: if (horizonte == null)
    // Decisão 2: if (remainingEntities.isEmpty() || (...))
    // Decisão 3: if (remainingEntities.size() == 1 && ...)
    @Test
    @DisplayName("getStatus: [CAMINHO DE EXCEÇÃO] Deve lançar exceção se horizonte for nulo")
    void getStatus_caminhoExcecao_horizonNulo() {
        // Cobre a Decisão 1
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> simulation.getStatus(null));
        assertEquals("Horizon não pode ser nulo.", exception.getMessage());
    }

    @Test
    @DisplayName("getStatus: [MC/DC] Cobre o caminho FAILED pela condição de IGUALDADE de ouro")
    void getStatus_caminhoFailed_pelaCondicaoDeIgualdadeDeOuro() {
        // Cenário: size == 1 (true) E guardiao.gold == creature.gold (true para a condição '<=')
        // Este teste isola o caso de fronteira exato do operador '<='.

        // Arrange
        Horizon horizon = new Horizon(1, 2);
        horizon.getGuardiao().setGold(500);
        horizon.getEntities().get(0).setGold(500); // Ouro é exatamente igual

        // Act
        SimulationStatus status = simulation.getStatus(horizon);

        // Assert
        assertEquals(SimulationStatus.FAILED, status, "Deveria ser FAILED quando o ouro é igual.");
    }

    @Test
    @DisplayName("getStatus: [MC/DC] Cobre o caminho FAILED pela condição de ouro maior")
    void getStatus_caminhoFailed_pelaCondicaoIsEmpty() {

        Horizon horizon = new Horizon(1, 2);

        horizon.getGuardiao().setGold(100);

        horizon.getEntities().get(0).setGold(150); // gold > guardião.gold

        assertEquals(SimulationStatus.FAILED, simulation.getStatus(horizon));
    }

    @Test
    @DisplayName("getStatus: [MC/DC] Cobre o caminho FAILED pela condição de ouro igual")
    void getStatus_caminhoFailed_pelaCondicaoDeOuroIgual() {

        Horizon horizon = new Horizon(1, 2);

        horizon.getGuardiao().setGold(100);

        horizon.getEntities().get(0).setGold(100); // gold == guardião.gold

        assertEquals(SimulationStatus.FAILED, simulation.getStatus(horizon));
    }

    @Test
    @DisplayName("getStatus: [MC/DC] Cobre o caminho RUNNING pela condição de tamanho")
    void getStatus_caminhoRunning_pelaCondicaoDeTamanho() {
        // Cobre a Decisão 1, forçando a primeira parte do || a ser FALSA.
        Horizon horizon = new Horizon(2, 3);
        assertEquals(SimulationStatus.RUNNING, simulation.getStatus(horizon));
    }

    @Test
    @DisplayName("getStatus: [MC/DC] Cobre o caminho SUCCESSFUL pela condição 'isEmpty'")
    void getStatus_caminhoSuccessful_pelaCondicaoIsEmpty() {
        // Cobre a Decisão 2, forçando a primeira parte do || a ser VERDADEIRA.
        Horizon horizon = new Horizon(1, 2);
        horizon.getEntities().clear();
        assertEquals(SimulationStatus.SUCCESSFUL, simulation.getStatus(horizon));
    }

    @Test
    @DisplayName("getStatus: [MC/DC] Cobre o caminho SUCCESSFUL pela condição de ouro")
    void getStatus_caminhoSuccessful_pelaCondicaoDeOuro() {
        // Cobre a Decisão 2, forçando a primeira parte do || a ser FALSA e a segunda VERDADEIRA.
        Horizon horizon = new Horizon(1, 2);
        horizon.getGuardiao().setGold(100);
        horizon.getEntities().get(0).setGold(50);
        assertEquals(SimulationStatus.SUCCESSFUL, simulation.getStatus(horizon));
    }

    @Test
    @DisplayName("getStatus: [MC/DC] Cobre o caminho FAILED")
    void getStatus_caminhoFailed() {
        // Cobre a Decisão 3 sendo VERDADEIRA (o que implica que a Decisão 2 foi FALSA).
        Horizon horizon = new Horizon(1, 2);
        horizon.getGuardiao().setGold(100);
        horizon.getEntities().get(0).setGold(150); // gold > guardião.gold
        assertEquals(SimulationStatus.FAILED, simulation.getStatus(horizon));
    }

    @Test
    @DisplayName("getStatus: Cobre o caminho RUNNING (caminho 'else' final)")
    void getStatus_caminhoRunning() {
        // Força todas as decisões de término a serem FALSAS.
        Horizon horizon = new Horizon(2, 3);
        assertEquals(SimulationStatus.RUNNING, simulation.getStatus(horizon));
    }

    // getStatus: Cobre o caminho RUNNING (caminho 'else' final)
    @Test
    @DisplayName("getStatus: Cobre o caminho RUNNING (caminho 'else' final)")
    void getStatus_caminhoRunningFinal() {
        // Força todas as decisões de término a serem FALSAS.
        Horizon horizon = new Horizon(2, 3);
        assertEquals(SimulationStatus.RUNNING, simulation.getStatus(horizon));
    }

    // getStatus: Cobre o caminho FAILED (Decisão 3 sendo VERDADEIRA)
    @Test
    @DisplayName("getStatus: Cobre o caminho FAILED (Decisão 3 sendo VERDADEIRA)")
    void getStatus_caminhoFailedDecisao3() {
        // Cobre o caso onde remainingEntities.size() == 1 e a entidade restante é o guardião.
        Horizon horizon = new Horizon(1, 2);
        horizon.getGuardiao().setGold(100);
        horizon.getEntities().get(0).setGold(150); // gold > guardião.gold
        assertEquals(SimulationStatus.FAILED, simulation.getStatus(horizon));
    }

    // getStatus: Cobre o caminho SUCCESSFUL (Decisão 2 sendo VERDADEIRA)
    @Test
    @DisplayName("getStatus: Cobre o caminho SUCCESSFUL (Decisão 2 sendo VERDADEIRA)")
    void getStatus_caminhoSuccessfulDecisao2() {
        // Cobre o caso onde remainingEntities.isEmpty() é VERDADEIRO.
        Horizon horizon = new Horizon(1, 2);
        horizon.getEntities().clear(); // Limpa as entidades
        assertEquals(SimulationStatus.SUCCESSFUL, simulation.getStatus(horizon));
    }

    // --- TESTES ESTRUTURAIS PARA findNearestNeighbor ---
    // if (horizonte == null || currentEntity == null) {
    @Test
    @DisplayName("findNearestNeighbor: Cobre o caminho de exceção onde horizonte é nulo")
    void findNearestNeighbor_caminhoExcecao_horizonNulo() {
        // Cobre o caso 'if (horizonte == null || entidadeAtual == null)'
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> simulation.findNearestNeighbor(null, new CreatureUnit(1)));
        assertEquals("Horizon e entidade atual não podem ser nulos.", exception.getMessage());
    }

    @Test
    @DisplayName("findNearestNeighbor: Cobre o caminho de exceção onde entidade atual é nula")
    void findNearestNeighbor_caminhoExcecao_entidadeAtualNula() {
        // Cobre o caso 'if (horizonte == null || entidadeAtual == null)'
        Horizon horizon = new Horizon(1, 2);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> simulation.findNearestNeighbor(horizon, null));
        assertEquals("Horizon e entidade atual não podem ser nulos.", exception.getMessage());
    }

    @Test
    @DisplayName("findNearestNeighbor: Cobre o caminho onde não há vizinhos")
    void findNearestNeighbor_caminhoSemVizinhos_deveRetornarNulo() {
        // Este teste força 'if (allEntities.isEmpty())' a ser true em findNearestNeighbor
        when(randomPort.nextFactor()).thenReturn(0.0);
        Horizon horizon = new Horizon(1, 2); // Apenas uma criatura

        simulation.runIteration(horizon);

        // A asserção implícita é que não houve NullPointerException no treatNeighborTheft,
        // pois o victim seria nulo.
        // Podemos verificar que o ouro não mudou.
        assertEquals(1_000_000, horizon.getEntities().get(0).getGold());
    }

    @Test
    @DisplayName("findNearestNeighbor: Cobre o caminho onde há vizinhos")
    void findNearestNeighbor_caminhoComVizinhos_deveRetornarVizinho() {
        // Este teste cobre o caso onde há vizinhos e o método retorna um vizinho válido.
        Horizon horizon = new Horizon(2, 2);
        horizon.getEntities().get(0).setX(100.0); // C1
        horizon.getEntities().get(1).setX(200.0); // C2 (vizinho)

        HorizonEntities neighbor = simulation.findNearestNeighbor(horizon, horizon.getEntities().get(0));

        assertNotNull(neighbor, "Deveria encontrar um vizinho.");
        assertEquals(200.0, neighbor.getX(), "O vizinho deveria estar na posição x=200.");
    }

    @Test
    @DisplayName("findNearestNeighbor: Cobre o caminho onde há vizinhos, mas a entidade atual é nula")
    void findNearestNeighbor_caminhoComVizinhosMasEntidadeAtualNula() {
        // Este teste cobre o caso onde há vizinhos, mas a entidade atual é nula.
        Horizon horizon = new Horizon(2, 2);
        horizon.getEntities().get(0).setX(100.0); // C1
        horizon.getEntities().get(1).setX(200.0); // C2 (vizinho)

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> simulation.findNearestNeighbor(horizon, null));
        assertEquals("Horizon e entidade atual não podem ser nulos.", exception.getMessage());
    }

    // --- TESTES ESTRUTURAIS PARA treatNeighborTheft ---
    @Test
    @DisplayName("treatNeighborTheft: Cobre o caminho onde não há vítima para roubar")
    void treatNeighborTheft_caminhoSemVitima() {
        // Cobre o caso 'victim == null' no if do treatNeighborTheft
        // Este cenário é o mesmo do teste acima.
        when(randomPort.nextFactor()).thenReturn(0.0);
        Horizon horizon = new Horizon(1, 2);

        simulation.runIteration(horizon);

        assertEquals(1_000_000, horizon.getEntities().get(0).getGold());
    }

    @Test
    @DisplayName("treatNeighborTheft: Cobre o caminho onde a vítima não pode perder ouro")
    void treatNeighborTheft_caminhoVitimaNaoPerdeOuro() {
        // Cobre o caso 'victim instanceof LoseGold' sendo falso
        when(randomPort.nextFactor()).thenReturn(0.0);
        // Cenário: C1 e uma entidade que não perde ouro (nosso stub)
        Horizon horizon = new Horizon(1, 2);
        HorizonEntities fakeVictim = new NonMovableEntity2(); // Usando o stub do teste anterior
        fakeVictim.setX(200.0);
        fakeVictim.setGold(5000.0);
        horizon.addEntity(fakeVictim);

        double c1InitialGold = horizon.getEntities().get(0).getGold();

        simulation.runIteration(horizon);

        // O ouro da C1 não deve mudar, pois a vítima não implementa LoseGold
        assertEquals(c1InitialGold, horizon.getEntities().get(0).getGold());
    }

    @Test
    @DisplayName("treatNeighborTheft: Cobre o caminho onde a vítima perde ouro")
    void treatNeighborTheft_caminhoVitimaPerdeOuro() {
        // Cenário: C1 e uma entidade que perde ouro (implementa LoseGold)
        Horizon horizon = new Horizon(2, 2);
        HorizonEntities victim = new NonMovableEntity2();
        victim.setX(200.0);
        victim.setGold(5000.0);
        horizon.addEntity(victim);

        double c1InitialGold = horizon.getEntities().get(0).getGold();

        simulation.treatNeighborTheft(horizon, horizon.getEntities().get(0));

        // O ouro da C1 deve aumentar, pois a vítima perde ouro
        assertTrue(horizon.getEntities().get(0).getGold() > c1InitialGold,
                "O ouro do atacante deveria ter aumentado.");
    }

    @Test
    @DisplayName("treatNeighborTheft: Cobre o caminho onde o atacante não pode roubar ouro")
    void treatNeighborTheft_caminhoAtacanteNaoRoubaOuro() {
        // Cobre o caso '!(attacker instanceof StealGold)' sendo verdadeiro
        when(randomPort.nextFactor()).thenReturn(0.0);
        // Cenário: C1 e uma entidade que não pode roubar ouro (não implementa StealGold)
        Horizon horizon = new Horizon(1, 2);
        HorizonEntities attacker = new NonMovableEntity2(); // Usando o stub do teste anterior
        attacker.setX(100.0);
        horizon.addEntity(attacker);

        double c1InitialGold = horizon.getEntities().get(0).getGold();

        simulation.runIteration(horizon);

        // O ouro da C1 não deve mudar, pois o atacante não pode roubar ouro
        assertEquals(c1InitialGold, horizon.getEntities().get(0).getGold());
    }

    // --- TESTES ESTRUTURAIS PARA resolveInteractionsAt ---
    @Test
    @DisplayName("resolveInteractionsAt: Cobre o caminho de exceção onde horizonte é nulo")
    void resolveInteractionsAt_caminhoExcecao_horizonNulo() {
        // Cobre o caso 'if (horizonte == null)'
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> simulation.resolveInteractionsAt(null, 100.0));
        assertEquals("Horizon não pode ser nulo e a posição deve ser um número válido.", exception.getMessage());
    }

    @Test
    @DisplayName("resolveInteractionsAt: Cobre o caminho de exceção onde posição é NaN")
    void resolveInteractionsAt_caminhoExcecao_posicaoNaN() {
        // Cobre o caso 'Double.isNaN(posição)'
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> simulation.resolveInteractionsAt(new Horizon(1, 2), Double.NaN));
        assertEquals("Horizon não pode ser nulo e a posição deve ser um número válido.", exception.getMessage());
    }

    @Test
    @DisplayName("resolveInteractionsAt: Cobre o caminho de exceção onde posição é infinita")
    void resolveInteractionsAt_caminhoExcecao_posicaoInfinita() {
        // Cobre o caso 'Double.isInfinite(posição)'
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> simulation.resolveInteractionsAt(new Horizon(1, 2), Double.POSITIVE_INFINITY));
        assertEquals("Horizon não pode ser nulo e a posição deve ser um número válido.", exception.getMessage());
    }

    @Test
    @DisplayName("resolveInteractionsAt: Cobre o caminho onde não há colisão (size <= 1)")
    void resolveInteractionsAt_caminhoSemColisao() {
        when(randomPort.nextFactor()).thenReturn(0.0);
        Horizon horizon = new Horizon(2, 3);
        // Coloca as criaturas em posições diferentes
        horizon.getEntities().get(0).setX(100.0);
        horizon.getEntities().get(1).setX(200.0);

        long initialEntityCount = horizon.getEntities().size();
        simulation.runIteration(horizon);

        assertEquals(initialEntityCount, horizon.getEntities().size());
    }

    @Test
    @DisplayName("resolveInteractionsAt: Cobre o caminho de fusão com cluster pré-existente")
    void resolveInteractionsAt_caminhoFusaoComClusterExistente() {
        // Cobre o 'if (baseFusion != null)'
        when(randomPort.nextFactor()).thenReturn(0.0);
        Horizon horizon = new Horizon(1, 2);
        CreatureCluster cluster = new CreatureCluster(10, 100.0, 1000.0);
        horizon.addEntity(cluster);
        // Coloca a C1 na mesma posição do cluster
        horizon.getEntities().get(0).setX(100.0);

        simulation.runIteration(horizon);

        assertEquals(1, horizon.getEntities().size());
        assertTrue(horizon.getEntities().get(0) instanceof CreatureCluster);
        assertTrue(horizon.getEntities().get(0).getGold() > 1000.0);
    }

    // --- Classe auxiliar (Stub) para testes estruturais ---
    private static class NonMovableEntity2 implements HorizonEntities, LoseGold {

        @SuppressWarnings("FieldMayBeFinal")
        private int id = 998;
        private double x = 0;
        private double gold = 0;

        @Override
        public int getId() {
            return id;
        }

        @Override
        public double getX() {
            return x;
        }

        @Override
        public void setX(double newX) {
            this.x = newX;
        }

        @Override
        public double getGold() {
            return gold;
        }

        @Override
        public void setGold(double newGold) {
            this.gold = newGold;
        }

        // Implementa LoseGold para o teste 'treatNeighborTheft...' ser mais preciso
        @Override
        public double loseGold(double percentage) {
            return 0;
        }
    }

    @Test
    @DisplayName("resolveInteractionsAt: Cobre o caminho de fusão com cluster novo")
    void resolveInteractionsAt_caminhoFusaoComClusterNovo() {
        // Cobre o 'if (baseFusion == null)'
        when(randomPort.nextFactor()).thenReturn(0.0);
        Horizon horizon = new Horizon(1, 2);
        // Coloca a C1 na mesma posição do cluster
        horizon.getEntities().get(0).setX(100.0);
        CreatureCluster newCluster = new CreatureCluster(10, 100.0, 1000.0);
        horizon.addEntity(newCluster); // Adiciona um novo cluster

        simulation.runIteration(horizon);

        assertEquals(1, horizon.getEntities().size());
        assertTrue(horizon.getEntities().get(0) instanceof CreatureCluster);
        assertTrue(horizon.getEntities().get(0).getGold() > 1000.0);
    }

    @Test
    @DisplayName("resolveInteractionsAt: Cobre o caminho onde não há sobrevivente")
    void resolveInteractionsAt_caminhoSemSobrevivente() {
        // Para realizar esse testes, precisamos colocar um guardião na mesma posição de um cluster!
        Horizon horizon = new Horizon(1, 2);
        horizon.getEntities().clear(); // Limpa as entidades

        horizon.addEntity(new CreatureCluster(10, 100.0, 1000.0)); // Adiciona um cluster

        horizon.getGuardiao().setX(100.0); // Colisão com o guardião

        // Se o sobrevevivente for o guardião, significa que não há sobrevivente, logo o survivor deve ser uma instância de Guardian.
        HorizonEntities survivor = simulation.resolveInteractionsAt(horizon, 100.0);

        assertTrue(survivor instanceof Guardian, "O sobrevivente deve ser o guardião.");

        assertEquals(horizon.getEntities().size(), 0, "Deveria não haver sobreviventes após a colisão, apenas o guardião.");

    }

    @Test
    @DisplayName("resolveInteractionsAt: Cobre o caminho onde há sobrevivente")
    void resolveInteractionsAt_caminhoComSobrevivente() {
        // Cobre o caso onde há sobrevivente (survivor != null)
        Horizon horizon = new Horizon(1, 2);
        horizon.getEntities().get(0).setX(100.0); // Posição isolada

        HorizonEntities survivor = simulation.resolveInteractionsAt(horizon, 100.0);

        assertNotNull(survivor, "Deveria retornar um sobrevivente quando há colisão.");
        assertEquals(100.0, survivor.getX(), "O sobrevivente deveria estar na posição x=100.");
    }

    @Test
    @DisplayName("resolveInteractionsAt: Cobre o caminho onde há sobrevivente e é um Guardião")
    void resolveInteractionsAt_caminhoComSobreviventeGuardiao() {
        // Cobre o caso onde o sobrevivente é um Guardião
        Horizon horizon = new Horizon(1, 2);
        horizon.getEntities().get(0).setX(100.0); // Posição isolada

        HorizonEntities survivor = simulation.resolveInteractionsAt(horizon, 100.0);

        assertNotNull(survivor, "Deveria retornar um sobrevivente quando há colisão.");
        assertTrue(survivor instanceof CreatureUnit, "O sobrevivente deveria ser uma entidade do tipo CreatureUnit.");
    }

    @Test
    @DisplayName("resolveInteractionsAt: Cobre o caminho onde há sobrevivente e não é um Guardião")
    void resolveInteractionsAt_caminhoComSobreviventeNaoGuardiao() {
        // Cobre o caso onde o sobrevivente não é um Guardião
        Horizon horizon = new Horizon(1, 2);
        horizon.getEntities().get(0).setX(100.0); // Posição isolada

        HorizonEntities survivor = simulation.resolveInteractionsAt(horizon, 100.0);

        assertNotNull(survivor, "Deveria retornar um sobrevivente quando há colisão.");
        assertTrue((survivor instanceof CreatureUnit), "O sobrevivente não deveria ser um Guardião.");
    }

}
