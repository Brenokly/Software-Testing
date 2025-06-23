package com.simulador.criaturas.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testes de Domínio para a classe CreatureUnit")
public class CreatureUnitTest {

    private CreatureUnit creature;

    @BeforeEach
    public void setUp() {
        creature = new CreatureUnit(1);
    }

    //----------------------------------------------------------------------------------------------------------------
    // Método MOVE
    @Test
    @DisplayName("Deve mover para frente corretamente quando o fator R é positivo")
    void deveMoverCorretamenteQuandoFatorEPositivo() {
        // Estado inicial definido
        creature.setGold(1000.0);
        creature.setX(50.0);

        double expectedPosition = 50.0 + (0.5 * 1000.0); // 50 + 500 = 550.0

        creature.move(0.5);

        assertEquals(expectedPosition, creature.getX(), "A posição X da criatura não foi atualizada corretamente.");
    }

    @Test
    @DisplayName("Deve mover para trás corretamente quando o fator R é negativo")
    void deveMoverCorretamenteQuandoFatorENegativo() {
        creature.setGold(500.0);
        creature.setX(200.0);
        double expectedPosition = 200.0 + (-1.0 * 500.0); // 200 - 500 = -300.0

        creature.move(-1.0);

        assertEquals(expectedPosition, creature.getX());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para fator R maior que 1")
    void deveLancarExcecaoParaFatorMaiorQueUm() {
        double invalidFactor = 1.1;
        // Esta é a mensagem exata que você definiu no seu método 'move'
        String expectedErrorMessage = "Valor aleatório deve estar entre -1 e 1.";

        // 1. Capture a exceção retornada em uma variável 'exception'
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            creature.move(invalidFactor);
        });

        // 2. Verifique se a mensagem da exceção capturada é a que você esperava
        String actualMessage = exception.getMessage();
        assertEquals(expectedErrorMessage, actualMessage);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para fator R menor que -1")
    void deveLancarExcecaoParaFatorMenorQueMenosUm() {
        double invalidFactor = -1.5;
        String expectedErrorMessage = "Valor aleatório deve estar entre -1 e 1.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            creature.move(invalidFactor);
        });

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    //----------------------------------------------------------------------------------------------------------------
    // Método StealGold
    @Test
    @DisplayName("Deve Roubar Ouro Corretamente Quando Quantidade Roubada é Positiva")
    void deveRoubarOuroCorretamenteQuandoQuantidadeRoubadaEPositiva() {
        creature.setGold(100.0);
        double amountStolen = 50.0;
        double expectedGold = 150.0;

        double stolen = creature.stealGold(amountStolen);

        assertEquals(amountStolen, stolen, "A quantidade roubada não é a esperada.");
        assertEquals(expectedGold, creature.getGold(), "O ouro da criatura não foi atualizado corretamente.");
    }

    @Test
    @DisplayName("Deve Retornar Zero Quando Quantidade Roubada é Zero")
    void deveRetornarZeroQuandoQuantidadeRoubadaEZero() {
        creature.setGold(100.0);
        double amountStolen = 0.0;
        double expectedGold = 100.0;

        double stolen = creature.stealGold(amountStolen);

        assertEquals(0.0, stolen, "A quantidade roubada deve ser zero.");
        assertEquals(expectedGold, creature.getGold(), "O ouro da criatura não deve ser alterado.");
    }

    @Test
    @DisplayName("Deve Retornar Zero Quando Quantidade Roubada é Negativa")
    void deveRetornarZeroQuandoQuantidadeRoubadaENegativa() {
        creature.setGold(100.0);
        double amountStolen = -50.0;
        double expectedGold = 100.0;

        double stolen = creature.stealGold(amountStolen);

        assertEquals(0.0, stolen, "A quantidade roubada deve ser zero.");
        assertEquals(expectedGold, creature.getGold(), "O ouro da criatura não deve ser alterado.");
    }

    @Test
    @DisplayName("Deve Lançar IllegalArgumentException Quando Quantidade Roubada é Infinita")
    void deveLancarExcecaoQuandoQuantidadeRoubadaEInfinita() {
        creature.setGold(100.0);
        double amountStolen = Double.POSITIVE_INFINITY;

        String expectedErrorMessage = "Roubo inválido: valor roubado não pode ser infinito.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            creature.stealGold(amountStolen);
        });

        assertEquals(expectedErrorMessage, exception.getMessage(), "A mensagem de erro não é a esperada.");
    }

    //----------------------------------------------------------------------------------------------------------------
    // Método LoseGold
    @Test
    @DisplayName("Deve Perder Ouro Corretamente Quando Percentual é Válido")
    void devePerderOuroCorretamenteQuandoPercentualEVálido() {
        creature.setGold(1000.0);
        double percentage = 0.2; // 20%
        double expectedLost = 200.0; // 20% de 1000
        double expectedRemainingGold = 800.0;

        double lost = creature.loseGold(percentage);

        assertEquals(expectedLost, lost, "A quantidade de ouro perdida não é a esperada.");
        assertEquals(expectedRemainingGold, creature.getGold(), "O ouro restante não foi atualizado corretamente.");
    }

    @Test
    @DisplayName("Deve Lançar IllegalArgumentException Quando Percentual é Menor ou Igual a Zero")
    void deveLancarExcecaoQuandoPercentualMenorOuIgualAZero() {
        creature.setGold(1000.0);
        double invalidPercentage = 0.0; // 0% não é válido

        String expectedErrorMessage = "Percentual de perda deve estar entre 0 e 1 (exclusivo de 0).";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            creature.loseGold(invalidPercentage);
        });

        assertEquals(expectedErrorMessage, exception.getMessage(), "A mensagem de erro não é a esperada.");
    }

    @Test
    @DisplayName("Deve Lançar IllegalArgumentException Quando Percentual é Maior que 1")
    void deveLancarExcecaoQuandoPercentualMaiorQueUm() {
        creature.setGold(1000.0);
        double invalidPercentage = 1.5; // 150% não é válido

        String expectedErrorMessage = "Percentual de perda deve estar entre 0 e 1 (exclusivo de 0).";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            creature.loseGold(invalidPercentage);
        });

        assertEquals(expectedErrorMessage, exception.getMessage(), "A mensagem de erro não é a esperada.");
    }

    @Test
    @DisplayName("Deve Retornar Zero Quando Ouro é Zero e Percentual é Válido")
    void deveRetornarZeroQuandoOuroEZeroEPercentualEVálido() {
        creature.setGold(0.0);
        double percentage = 0.5; // 50%

        double lost = creature.loseGold(percentage);

        assertEquals(0.0, lost, "A quantidade de ouro perdida deve ser zero quando o ouro é zero.");
        assertEquals(0.0, creature.getGold(), "O ouro restante deve ser zero.");
    }

    @Test
    @DisplayName("Deve Lançar IllegalArgumentException Quando Ouro é Zero e Percentual é Invalido")
    void deveLancarExcecaoQuandoOuroEZeroEPercentualEInvalido() {
        creature.setGold(0.0);
        double invalidPercentage = 1.5; // 150% não é válido

        String expectedErrorMessage = "Percentual de perda deve estar entre 0 e 1 (exclusivo de 0).";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            creature.loseGold(invalidPercentage);
        });

        assertEquals(expectedErrorMessage, exception.getMessage(), "A mensagem de erro não é a esperada.");
    }
}
