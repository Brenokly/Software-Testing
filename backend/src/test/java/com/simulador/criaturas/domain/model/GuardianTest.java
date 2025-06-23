package com.simulador.criaturas.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testes de Domínio para a classe Guardian")
public class GuardianTest {

    private Guardian guardian;

    @BeforeEach
    public void setUp() {
        guardian = new Guardian(1);
    }

    //----------------------------------------------------------------------------------------------------------------
    // Método MOVE
    @Test
    @DisplayName("Deve mover para frente corretamente quando o fator R é positivo")
    void deveMoverCorretamenteQuandoFatorEPositivo() {
        // Estado inicial definido
        guardian.setGold(1000.0);
        guardian.setX(50.0);

        double expectedPosition = 50.0 + (0.5 * 1000.0); // 50 + 500 = 550.0

        guardian.move(0.5);

        assertEquals(expectedPosition, guardian.getX(), "A posição X da criatura não foi atualizada corretamente.");
    }

    @Test
    @DisplayName("Deve mover para trás corretamente quando o fator R é negativo")
    void deveMoverCorretamenteQuandoFatorENegativo() {
        guardian.setGold(500.0);
        guardian.setX(200.0);
        double expectedPosition = 200.0 + (-1.0 * 500.0); // 200 - 500 = -300.0

        guardian.move(-1.0);

        assertEquals(expectedPosition, guardian.getX());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para fator R maior que 1")
    void deveLancarExcecaoParaFatorMaiorQueUm() {
        double invalidFactor = 1.1;
        // Esta é a mensagem exata que você definiu no seu método 'move'
        String expectedErrorMessage = "Valor aleatório deve estar entre -1 e 1.";

        // 1. Capture a exceção retornada em uma variável 'exception'
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            guardian.move(invalidFactor);
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
            guardian.move(invalidFactor);
        });

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    //----------------------------------------------------------------------------------------------------------------
    // Método StealGold
    @Test
    @DisplayName("Deve Roubar Ouro Corretamente Quando Quantidade Roubada é Positiva")
    void deveRoubarOuroCorretamenteQuandoQuantidadeRoubadaEPositiva() {
        guardian.setGold(100.0);
        double amountStolen = 50.0;
        double expectedGold = 150.0;

        double stolen = guardian.stealGold(amountStolen);

        assertEquals(amountStolen, stolen, "A quantidade roubada não é a esperada.");
        assertEquals(expectedGold, guardian.getGold(), "O ouro da criatura não foi atualizado corretamente.");
    }

    @Test
    @DisplayName("Deve Retornar Zero Quando Quantidade Roubada é Zero")
    void deveRetornarZeroQuandoQuantidadeRoubadaEZero() {
        guardian.setGold(100.0);
        double amountStolen = 0.0;
        double expectedGold = 100.0;

        double stolen = guardian.stealGold(amountStolen);

        assertEquals(0.0, stolen, "A quantidade roubada deve ser zero.");
        assertEquals(expectedGold, guardian.getGold(), "O ouro da criatura não deve ser alterado.");
    }

    @Test
    @DisplayName("Deve Retornar Zero Quando Quantidade Roubada é Negativa")
    void deveRetornarZeroQuandoQuantidadeRoubadaENegativa() {
        guardian.setGold(100.0);
        double amountStolen = -50.0;
        double expectedGold = 100.0;

        double stolen = guardian.stealGold(amountStolen);

        assertEquals(0.0, stolen, "A quantidade roubada deve ser zero.");
        assertEquals(expectedGold, guardian.getGold(), "O ouro da criatura não deve ser alterado.");
    }

    @Test
    @DisplayName("Deve Lançar IllegalArgumentException Quando Quantidade Roubada é Infinita")
    void deveLancarExcecaoQuandoQuantidadeRoubadaEInfinita() {
        guardian.setGold(100.0);
        double amountStolen = Double.POSITIVE_INFINITY;

        String expectedErrorMessage = "Roubo inválido: valor roubado não pode ser infinito.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            guardian.stealGold(amountStolen);
        });

        assertEquals(expectedErrorMessage, exception.getMessage(), "A mensagem de erro não é a esperada.");
    }
}
