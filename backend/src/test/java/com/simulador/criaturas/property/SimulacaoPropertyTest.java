package com.simulador.criaturas.property;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.simulador.criaturas.domain.behaviors.HorizonEntities;
import com.simulador.criaturas.domain.model.Horizon;
import com.simulador.criaturas.domain.port.out.RandomPort;
import com.simulador.criaturas.domain.service.Simulation;
import com.simulador.criaturas.utils.SimulationStatus;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.constraints.IntRange;

class SimulationPropertyTest {

    // --- Dublê de Teste (Stub) para controlar a aleatoriedade ---
    private static class FixedRandomPort implements RandomPort {

        private final double factor;

        FixedRandomPort(double factor) {
            this.factor = factor;
        }

        @Override
        public double nextFactor() {
            return factor;
        }
    }

    // --- PROPRIEDADES PARA createNewSimulation ---
    @Property
    void createNewSimulationSempreResultaEmEstadoValido(
            // Gera um número de criaturas dentro do intervalo permitido pelo método
            @ForAll @IntRange(min = 1, max = 10) int numeroDeCriaturas
    ) {
        // Arrange
        Simulation simulation = new Simulation(new FixedRandomPort(0.0));

        // Act
        Horizon horizon = simulation.createNewSimulation(numeroDeCriaturas);

        // Assert
        // Propriedades que devem ser SEMPRE verdadeiras
        assertNotNull(horizon);
        assertEquals(numeroDeCriaturas, horizon.getEntities().size());
        assertEquals(SimulationStatus.RUNNING, horizon.getStatus());
        assertNotNull(horizon.getGuardiao());
    }

    // --- PROPRIEDADES PARA runIteration ---
    @Property
    void numeroDeEntidadesNuncaAumentaAposIteracao(
            // Usa nosso gerador customizado de Horizontes
            @ForAll("horizonsValidosParaIteracao") Horizon horizon
    ) {
        // Arrange
        // Usamos um fator aleatório válido para o movimento
        Simulation simulation = new Simulation(new FixedRandomPort(0.5));
        int tamanhoInicial = horizon.getEntities().size();

        // Act
        simulation.runIteration(horizon);

        // Assert
        int tamanhoFinal = horizon.getEntities().size();
        // A propriedade é que o tamanho final é sempre menor ou igual ao inicial
        assertTrue(tamanhoFinal <= tamanhoInicial, "O número de entidades não deveria aumentar.");
    }

    @Property
    void quantidadeTotalDeOuroESempreConservada(
            @ForAll("horizonsValidosParaIteracao") Horizon horizon
    ) {
        // Esta é a propriedade mais poderosa! Ela testa indiretamente toda a sua
        // lógica de fusão, roubo e eliminação de uma só vez.

        // Arrange
        Simulation simulation = new Simulation(new FixedRandomPort(0.2));
        double ouroTotalInicial = calculateTotalGold(horizon);

        // Act
        simulation.runIteration(horizon);

        // Assert
        double ouroTotalFinal = calculateTotalGold(horizon);
        // A propriedade é que, como o ouro só muda de mãos, o total no sistema
        // deve ser o mesmo antes e depois.
        assertEquals(ouroTotalInicial, ouroTotalFinal, 0.00001, "O ouro total do sistema deveria ser conservado.");
    }

    // --- MÉTODOS AUXILIARES E PROVIDERS ---
    private double calculateTotalGold(Horizon horizon) {
        double entitiesGold = horizon.getEntities().stream()
                .mapToDouble(HorizonEntities::getGold)
                .sum();
        double guardianGold = (horizon.getGuardiao() != null) ? horizon.getGuardiao().getGold() : 0;
        return entitiesGold + guardianGold;
    }

    // Provider que gera Horizontes válidos e prontos para a iteração.
    @Provide
    Arbitrary<Horizon> horizonsValidosParaIteracao() {
        // Máquina que gera um número de criaturas
        Arbitrary<Integer> size = Arbitraries.integers().between(2, 15);

        // Para cada tamanho gerado, cria um Horizonte e define estados aleatórios
        return size.map(s -> {
            // Corrige o tamanho para estar dentro do limite do createNewSimulation
            int validSize = Math.min(s, 10);
            Simulation tempSim = new Simulation(new FixedRandomPort(0));
            Horizon horizon = tempSim.createNewSimulation(validSize);

            // Define ouro e posições aleatórias para as entidades e o guardião
            for (HorizonEntities entity : horizon.getEntities()) {
                entity.setGold(Arbitraries.doubles().between(100, 10000).sample());
                entity.setX(Arbitraries.integers().between(0, 5).sample() * 100.0);
            }
            if (horizon.getGuardiao() != null) {
                horizon.getGuardiao().setGold(Arbitraries.doubles().between(0, 5000).sample());
                horizon.getGuardiao().setX(Arbitraries.integers().between(0, 5).sample() * 100.0);
            }
            horizon.setStatus(SimulationStatus.RUNNING);
            return horizon;
        });
    }
}
