package com.simulador.criaturas.property;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;

import com.simulador.criaturas.domain.model.Guardian;

import net.jqwik.api.Assume;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.api.constraints.Positive;

@DisplayName("Testes de Propriedade para os Comportamentos de Guardian")
public class GuardianPropertyTest {

    // --- PROPRIEDADE PARA O MÉTODO move ---
    @Property
    @DisplayName("Propriedade: Mover um guardian nunca deve alterar seu ouro.")
    void moveNuncaAlteraOuro(
            // @ForAll gera valores aleatórios para este parâmetro.
            // @DoubleRange limita os valores gerados ao intervalo válido do nosso método.
            @ForAll @DoubleRange(min = -1.0, max = 1.0) double randomR
    ) {
        // Arrange
        Guardian guardian = new Guardian(1);
        double initialGold = 1000.0;
        guardian.setGold(initialGold);

        // Act
        guardian.move(randomR);

        // Assert
        // A propriedade que estamos testando é que o ouro é INVARIANTE à operação de mover.
        assertEquals(initialGold, guardian.getGold());
    }

    @Property
    @DisplayName("Propriedade: move() NUNCA deve lançar exceção para entradas válidas")
    void moveNaoLancaExcecaoParaEntradasValidas(
            // Gerador: Fornece QUALQUER double VÁLIDO entre -1 e 1
            @ForAll @DoubleRange(min = -1, max = 1) double validRandomR
    ) {
        // Arrange
        Guardian guardian = new Guardian(1);
        guardian.setGold(1000.0);

        // Act & Assert
        // A propriedade é que, para qualquer entrada válida, a execução completa sem erro.
        assertDoesNotThrow(() -> {
            guardian.move(validRandomR);
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
        Guardian guardian = new Guardian(1);

        // Act & Assert
        // A propriedade é que, para qualquer entrada inválida, uma exceção é garantida.
        assertThrows(IllegalArgumentException.class, () -> {
            guardian.move(anyRandomR);
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
        Assume.that(Double.isFinite(amountToSteal));

        // Arrange
        Guardian guardian = new Guardian(1);
        guardian.setGold(initialGold);

        // Act
        guardian.stealGold(amountToSteal);

        // Assert
        // A propriedade é que a operação é uma soma simples.
        assertEquals(initialGold + amountToSteal, guardian.getGold());
    }

    @Property
    @DisplayName("Propriedade: Roubar ouro negativo ou zero não altera o ouro do guardian.")
    void stealGoldNaoAlteraOuroParaValoresInvalidos(
            @ForAll double initialGold,
            @ForAll double amountToSteal
    ) {
        // Pré-condição para o teste: a lógica de stealGold não trata valores infinitos.
        Assume.that(Double.isFinite(amountToSteal));

        // Arrange
        Guardian guardian = new Guardian(1);
        guardian.setGold(initialGold);

        // Act
        guardian.stealGold(amountToSteal);

        // Assert
        // A propriedade é que, se amountToSteal <= 0, o ouro permanece inalterado.
        if (amountToSteal <= 0) {
            assertEquals(initialGold, guardian.getGold());
        } else {
            assertEquals(initialGold + amountToSteal, guardian.getGold());
        }
    }
}
