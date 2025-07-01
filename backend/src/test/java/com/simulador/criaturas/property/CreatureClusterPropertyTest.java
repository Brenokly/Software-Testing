package com.simulador.criaturas.property;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.simulador.criaturas.domain.behaviors.HorizonEntities;
import com.simulador.criaturas.domain.model.CreatureCluster;
import com.simulador.criaturas.domain.model.CreatureUnit;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Assume;
import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.api.constraints.Positive;

public class CreatureClusterPropertyTest {
// --- PROPRIEDADE PARA O MÉTODO move ---

    @Property
    void moveNuncaAlteraOuro(
            // @ForAll gera valores aleatórios para este parâmetro.
            // @DoubleRange limita os valores gerados ao intervalo válido do nosso método.
            @ForAll @DoubleRange(min = -1.0, max = 1.0) double randomR
    ) {
        // Arrange
        CreatureCluster creature = new CreatureCluster(1);
        double initialGold = 1000.0;
        creature.setGold(initialGold);

        // Act
        creature.move(randomR);

        // Assert
        // A propriedade que estamos testando é que o ouro é INVARIANTE à operação de mover.
        assertEquals(initialGold, creature.getGold());
    }

    @Property
    void moveNaoLancaExcecaoParaEntradasValidas(
            // Gerador: Fornece QUALQUER double VÁLIDO entre -1 e 1
            @ForAll @DoubleRange(min = -1, max = 1) double validRandomR
    ) {
        // Arrange
        CreatureCluster creature = new CreatureCluster(1);
        creature.setGold(1000.0);

        // Act & Assert
        // A propriedade é que, para qualquer entrada válida, a execução completa sem erro.
        assertDoesNotThrow(() -> {
            creature.move(validRandomR);
        });
    }

    @Property
    void moveSempreLancaExcecaoParaEntradasInvalidas(
            // Gerador: Fornece QUALQUER double
            @ForAll double anyRandomR
    ) {
        // Pré-condição do teste: só queremos rodar este teste para os números que SÃO inválidos.
        // Assume.that() diz ao jqwik: "Se esta condição não for satisfeita, pule esta tentativa e gere outro número".
        Assume.that(Double.isNaN(anyRandomR) || anyRandomR < -1 || anyRandomR > 1);

        // Arrange
        CreatureCluster creature = new CreatureCluster(1);

        // Act & Assert
        // A propriedade é que, para qualquer entrada inválida, uma exceção é garantida.
        assertThrows(IllegalArgumentException.class, () -> {
            creature.move(anyRandomR);
        });
    }

    // --- PROPRIEDADES PARA O MÉTODO loseGold ---
    @Property
    void loseGoldNuncaResultaEmOuroNegativo(
            @ForAll @Positive double initialGold,
            // Usamos o range completo, mas filtramos o caso de borda com Assume.that
            @ForAll @DoubleRange(min = 0.0, max = 1.0, maxIncluded = true) double validPercentage
    ) {
        // CORREÇÃO: Usamos Assume.that para dizer ao jqwik para pular o caso percentage=0,
        // que já é tratado por outro teste e pode causar o erro de "scale".
        Assume.that(validPercentage > 0);

        // Arrange
        CreatureUnit creature = new CreatureUnit(1);
        creature.setGold(initialGold);

        // Act
        creature.loseGold(validPercentage);

        // Assert
        assertTrue(creature.getGold() >= 0, "O ouro se tornou negativo: " + creature.getGold());
    }

    @Property
    void loseGoldLancaExcecaoParaPercentualInvalido(
            // Gerador: Fornece QUALQUER double
            @ForAll double anyRandomPercentage
    ) {
        // Pré-condição do teste: só queremos rodar este teste para os números que SÃO inválidos.
        // Assume.that() diz ao jqwik: "Se esta condição não for satisfeita, pule esta tentativa e gere outro número".
        Assume.that(anyRandomPercentage <= 0 || anyRandomPercentage > 1);

        // Arrange
        CreatureCluster creature = new CreatureCluster(1);
        creature.setGold(1000.0);

        // Act & Assert
        // A propriedade é que, para qualquer entrada inválida, uma exceção é garantida.
        assertThrows(IllegalArgumentException.class, () -> {
            creature.loseGold(anyRandomPercentage);
        });
    }

    // --- PROPRIEDADES PARA O MÉTODO stealGold ---
    @Property
    void stealGoldResultaNaSomaExata(
            @ForAll @Positive double initialGold,
            @ForAll @Positive double amountToSteal
    ) {
        // Pré-condição para o teste: a lógica de stealGold não trata valores infinitos.
        // Assume.that() diz ao jqwik para pular as tentativas com valores que não cumprem esta condição.
        Assume.that(Double.isFinite(amountToSteal));

        // Arrange
        CreatureCluster creature = new CreatureCluster(1);
        creature.setGold(initialGold);

        // Act
        creature.stealGold(amountToSteal);

        // Assert
        // A propriedade é que a operação é uma soma simples.
        assertEquals(initialGold + amountToSteal, creature.getGold());
    }

    @Property
    void stealGoldNaoAlteraOuroParaValoresInvalidos(
            @ForAll double initialGold,
            @ForAll double amountToSteal
    ) {
        // Pré-condição para o teste: a lógica de stealGold não trata valores infinitos.
        Assume.that(Double.isFinite(amountToSteal));

        // Arrange
        CreatureCluster creature = new CreatureCluster(1);
        creature.setGold(initialGold);

        // Act
        creature.stealGold(amountToSteal);

        // Assert
        // A propriedade é que, se amountToSteal <= 0, o ouro permanece inalterado.
        if (amountToSteal <= 0) {
            assertEquals(initialGold, creature.getGold());
        } else {
            assertEquals(initialGold + amountToSteal, creature.getGold());
        }
    }

    // --- PROPRIEDADES PARA O MÉTODO fusion ---
    @Property
    void fusionResultaNaSomaExata(
            // Gerador 1: Gera um valor de ouro inicial para o nosso cluster.
            @ForAll @Positive double clusterInitialGold,
            // Gerador 2: Gera um valor de ouro para a criatura que será absorvida.
            @ForAll @Positive double creatureGold
    ) {
        // Pré-condição: Garantimos que os valores gerados são finitos,
        // pois esta propriedade só testa o caminho feliz.
        Assume.that(Double.isFinite(clusterInitialGold) && Double.isFinite(creatureGold));

        // Arrange
        CreatureCluster cluster = new CreatureCluster(1, 0.0, clusterInitialGold);
        CreatureUnit creatureToAbsorb = new CreatureUnit(2, 0.0, creatureGold);
        double expectedFinalGold = clusterInitialGold + creatureGold;

        // Act
        cluster.fusion(creatureToAbsorb);

        // Assert
        // A propriedade é que a fusão é uma operação de soma precisa.
        assertEquals(expectedFinalGold, cluster.getGold(), 0.000001);
    }

    @Property
    void fusionSempreLancaExcecaoParaEntradasInvalidas(
            // Usamos um 'Provider' customizado para gerar apenas os casos inválidos que queremos testar.
            @ForAll("invalidCreatures") HorizonEntities invalidCreature
    ) {
        // Arrange
        CreatureCluster cluster = new CreatureCluster(1, 0.0, 1000.0);

        // Act & Assert
        // A propriedade é que, para qualquer uma dessas entradas inválidas, uma exceção é garantida.
        assertThrows(IllegalArgumentException.class, () -> {
            cluster.fusion(invalidCreature);
        });
    }

    // --- Provider para o Teste Acima ---
    // Um método anotado com @Provide cria um "gerador customizado" de dados para o jqwik.
    @Provide
    Arbitrary<HorizonEntities> invalidCreatures() {
        // Criamos uma criatura de teste com ouro infinito
        CreatureUnit creatureWithInfiniteGold = new CreatureUnit(99);
        creatureWithInfiniteGold.setGold(Double.POSITIVE_INFINITY);

        // Criamos uma criatura de teste com ouro NaN
        CreatureUnit creatureWithNaNGold = new CreatureUnit(98);
        creatureWithNaNGold.setGold(Double.NaN);

        // O Arbitraries.of() nos dá um gerador que sorteia um dos valores que passamos.
        // Neste caso, ele irá sortear ou 'null', ou a criatura com ouro infinito, ou a com ouro NaN.
        return Arbitraries.of(null, creatureWithInfiniteGold, creatureWithNaNGold);
    }
}
