package com.simulador.criaturas.property;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;

import com.simulador.criaturas.domain.model.CreatureUnit;

import net.jqwik.api.Assume;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.api.constraints.Positive;

@DisplayName("Testes de Propriedade para os Comportamentos de CreatureUnit")
public class CreatureUnitPropertyTest {

    // --- PROPRIEDADE PARA O MÉTODO move ---
    @Property
    @DisplayName("Propriedade: Mover uma criatura nunca deve alterar seu ouro.")
    void moveNuncaAlteraOuro(
            // @ForAll gera valores aleatórios para este parâmetro.
            // @DoubleRange limita os valores gerados ao intervalo válido do nosso método.
            @ForAll @DoubleRange(min = -1.0, max = 1.0) double randomR
    ) {
        // Arrange
        CreatureUnit creature = new CreatureUnit(1);
        double initialGold = 1000.0;
        creature.setGold(initialGold);

        // Act
        creature.move(randomR);

        // Assert
        // A propriedade que estamos testando é que o ouro é INVARIANTE à operação de mover.
        assertEquals(initialGold, creature.getGold());
    }

    @Property
    @DisplayName("Propriedade: move() NUNCA deve lançar exceção para entradas válidas")
    void moveNaoLancaExcecaoParaEntradasValidas(
            // Gerador: Fornece QUALQUER double VÁLIDO entre -1 e 1
            @ForAll @DoubleRange(min = -1, max = 1) double validRandomR
    ) {
        // Arrange
        CreatureUnit creature = new CreatureUnit(1);
        creature.setGold(1000.0);

        // Act & Assert
        // A propriedade é que, para qualquer entrada válida, a execução completa sem erro.
        assertDoesNotThrow(() -> {
            creature.move(validRandomR);
        });
    }

    @Property
    @DisplayName("Propriedade: move() SEMPRE deve lançar exceção para entradas inválidas")
    void moveSempreLancaExcecaoParaEntradasInvalidas(
            // Gerador: Fornece QUALQUER double
            @ForAll double anyRandomR
    ) {
        // Pré-condição do teste: só queremos rodar este teste para os números que SÃO inválidos.
        // Assume.that() diz ao jqwik: "Se esta condição não for satisfeita, pule esta tentativa e gere outro número".
        Assume.that(Double.isNaN(anyRandomR) || anyRandomR < -1 || anyRandomR > 1);

        // Arrange
        CreatureUnit creature = new CreatureUnit(1);

        // Act & Assert
        // A propriedade é que, para qualquer entrada inválida, uma exceção é garantida.
        assertThrows(IllegalArgumentException.class, () -> {
            creature.move(anyRandomR);
        });
    }

    // --- PROPRIEDADES PARA O MÉTODO loseGold ---
    @Property
    @DisplayName("Propriedade: Após perder ouro, o ouro final nunca é negativo.")
    void loseGoldNuncaResultaEmOuroNegativo(
            @ForAll @Positive double initialGold,
            @ForAll @DoubleRange(min = 0.0001, max = 1.0) double validPercentage
    ) {
        // Arrange
        CreatureUnit creature = new CreatureUnit(1);
        creature.setGold(initialGold);

        // Act
        creature.loseGold(validPercentage);

        // Assert
        // A propriedade é que o ouro nunca pode ficar abaixo de zero.
        assertTrue(creature.getGold() >= 0, "O ouro se tornou negativo: " + creature.getGold());
    }

    @Property
    @DisplayName("Propriedade: A soma do ouro final e do ouro perdido é igual ao ouro inicial.")
    void loseGoldPreservaASomaTotalDoOuro(
            @ForAll @Positive double initialGold,
            @ForAll @DoubleRange(min = 0.0001, max = 1.0) double validPercentage
    ) {
        // Esta é uma propriedade de "conservação de valor", muito poderosa.
        // Arrange
        CreatureUnit creature = new CreatureUnit(1);
        creature.setGold(initialGold);

        // Act
        double amountLost = creature.loseGold(validPercentage);
        double finalGold = creature.getGold();

        // Assert
        // A soma das partes (o que sobrou + o que foi perdido) deve ser igual ao todo original.
        // Usamos uma tolerância (delta) para comparações de 'double'.
        assertEquals(initialGold, finalGold + amountLost, 0.000001);
    }

    @Property
    @DisplayName("Propriedade: Perder ouro com percentual inválido sempre lança IllegalArgumentException.")
    void loseGoldLancaExcecaoParaPercentualInvalido(
            // Gerador: Fornece QUALQUER double
            @ForAll double anyRandomPercentage
    ) {
        // Pré-condição do teste: só queremos rodar este teste para os números que SÃO inválidos.
        // Assume.that() diz ao jqwik: "Se esta condição não for satisfeita, pule esta tentativa e gere outro número".
        Assume.that(anyRandomPercentage <= 0 || anyRandomPercentage > 1);

        // Arrange
        CreatureUnit creature = new CreatureUnit(1);
        creature.setGold(1000.0);

        // Act & Assert
        // A propriedade é que, para qualquer entrada inválida, uma exceção é garantida.
        assertThrows(IllegalArgumentException.class, () -> {
            creature.loseGold(anyRandomPercentage);
        });
    }

    // --- PROPRIEDADES PARA O MÉTODO stealGold ---
    @Property
    @DisplayName("Propriedade: Roubar uma quantia positiva sempre resulta na soma exata.")
    void stealGoldResultaNaSomaExata(
            @ForAll @Positive double initialGold,
            @ForAll @Positive double amountToSteal
    ) {
        // Pré-condição para o teste: a lógica de stealGold não trata valores infinitos.
        // Assume.that() diz ao jqwik para pular as tentativas com valores que não cumprem esta condição.
        Assume.that(Double.isFinite(amountToSteal));

        // Arrange
        CreatureUnit creature = new CreatureUnit(1);
        creature.setGold(initialGold);

        // Act
        creature.stealGold(amountToSteal);

        // Assert
        // A propriedade é que a operação é uma soma simples.
        assertEquals(initialGold + amountToSteal, creature.getGold());
    }

    @Property
    @DisplayName("Propriedade: Roubar ouro negativo ou zero não altera o ouro da creature.")
    void stealGoldNaoAlteraOuroParaValoresInvalidos(
            @ForAll double initialGold,
            @ForAll double amountToSteal
    ) {
        // Pré-condição para o teste: a lógica de stealGold não trata valores infinitos.
        Assume.that(Double.isFinite(amountToSteal));

        // Arrange
        CreatureUnit creature = new CreatureUnit(1);
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
}
