package com.simulador.criaturas.structural;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.simulador.criaturas.domain.model.CreatureCluster;
import com.simulador.criaturas.domain.model.CreatureUnit;

@DisplayName("Testes Estruturais (MC/DC) para os comportamentos de CreatureCluster")
public class CreatureClusterTestS {

    private CreatureCluster creatureCluster;

    @BeforeEach
    void setUp() {
        // Cria um cluster base para os testes
        creatureCluster = new CreatureCluster(100, 100.0, 1000.0);
    }

    // --- TESTES ESTRUTURAIS PARA O MÉTODO Fusion(HorizonEntities) ---
    // Decisão: if (creature == null)
    // Decisão: else if (!Double.isFinite(creature.getGold()))
    // Condições: C1: creature == null, C2: !isFinite(creature.getGold())
    @Test
    @DisplayName("fusion: Cobre o caminho principal com uma criatura válida")
    void fusion_caminhoPrincipal_quandoCriaturaEValida() {
        // Este teste força o primeiro 'if' a ser FALSO e o segundo 'if' a ser FALSO,
        // executando o caminho principal de soma de ouro.

        // Arrange
        CreatureUnit creatureParaAbsorver = new CreatureUnit(1, 0.0, 500.0);
        double expectedGold = creatureCluster.getGold() + creatureParaAbsorver.getGold(); // 1000 + 500 = 1500

        // Act
        creatureCluster.fusion(creatureParaAbsorver);

        // Assert
        assertEquals(expectedGold, creatureCluster.getGold());
    }

    @Test
    @DisplayName("fusion: Cobre o caminho de exceção quando a criatura é nula")
    void fusion_caminhoDeExcecao_quandoCriaturaForNula() {
        // Este teste força o primeiro 'if' (creature == null) a ser VERDADEIRO.

        // Arrange
        String expectedMessage = "Criatura não pode ser nula.";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            creatureCluster.fusion(null);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("fusion: Cobre o caminho da exceção quando o ouro da criatura não é finito")
    void fusion_caminhoDeExcecao_quandoOuroDaCriaturaNaoForFinito() {
        // Este teste força o primeiro 'if' a ser FALSO e o segundo 'if' (!isFinite) a ser VERDADEIRO.

        // Arrange
        CreatureUnit criaturaInvalida = new CreatureUnit(2, 0.0, Double.POSITIVE_INFINITY);
        String expectedMessage = "A quantidade de ouro da criatura deve ser um número finito.";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            creatureCluster.fusion(criaturaInvalida);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }

    // --- TESTES ESTRUTURAIS PARA O MÉTODO move(randomR) ---
    // Decisão: if (Double.isNaN(randomR) || randomR < -1 || randomR > 1)
    // Condições: C1: isNaN, C2: < -1, C3: > 1
    @Test
    @DisplayName("move: Cobre o caminho principal (else) onde o movimento ocorre")
    void move_caminhoPrincipal_quandoFatorEValido() {
        // Este teste força C1=false, C2=false, C3=false. A decisão do 'if' é FALSA.
        creatureCluster.setX(100);
        creatureCluster.move(0.5);
        assertEquals(600.0, creatureCluster.getX());
    }

    @Test
    @DisplayName("move: Cobre o caminho da exceção, isolando a condição isNaN (C1)")
    void move_caminhoDeExcecao_quandoFatorIsNaN() {
        // Este teste força C1=true. A decisão do 'if' é VERDADEIRA.
        // Par com o teste acima para MC/DC da condição C1.
        assertThrows(IllegalArgumentException.class, () -> creatureCluster.move(Double.NaN));
    }

    @Test
    @DisplayName("move: Cobre o caminho da exceção, isolando a condição < -1 (C2)")
    void move_caminhoDeExcecao_quandoFatorMenorQueMenosUm() {
        // Este teste força C2=true. A decisão do 'if' é VERDADEIRA.
        // Par com o teste de caminho principal para MC/DC da condição C2.
        assertThrows(IllegalArgumentException.class, () -> creatureCluster.move(-1.1));
    }

    @Test
    @DisplayName("move: Cobre o caminho da exceção, isolando a condição > 1 (C3)")
    void move_caminhoDeExcecao_quandoFatorMaiorQueUm() {
        // Este teste força C3=true. A decisão do 'if' é VERDADEIRA.
        // Par com o teste de caminho principal para MC/DC da condição C3.
        assertThrows(IllegalArgumentException.class, () -> creatureCluster.move(1.1));
    }

    // --- TESTES ESTRUTURAIS PARA O MÉTODO stealGold(amountStolen) ---
    // Decisão 1: if (!Double.isFinite(amountStolen))
    // Decisão 2: else if (amountStolen == 0 || amountStolen < 0)
    @Test
    @DisplayName("stealGold: Cobre o caminho principal (else final) onde o roubo ocorre")
    void stealGold_caminhoPrincipal_quandoQuantiaEValida() {
        // Cobre o 'else' da Decisão 1 e o 'else' da Decisão 2.
        creatureCluster.stealGold(100.0);
        assertEquals(1100.0, creatureCluster.getGold());
    }

    @Test
    @DisplayName("stealGold: Cobre o caminho da exceção para valor não finito")
    void stealGold_caminhoDeExcecao_quandoQuantiaNaoFinita() {
        // Cobre o 'if' da Decisão 1.
        assertThrows(IllegalArgumentException.class, () -> creatureCluster.stealGold(Double.POSITIVE_INFINITY));
    }

    @Test
    @DisplayName("stealGold: Cobre o caminho de retorno zero, isolando a condição == 0")
    void stealGold_caminhoRetornoZero_quandoQuantiaIgualAZero() {
        // Cobre o 'else if', focando na condição C1 (== 0). Par com o teste principal.
        double result = creatureCluster.stealGold(0.0);
        assertEquals(0.0, result);
        assertEquals(1000.0, creatureCluster.getGold()); // Garante que o ouro não mudou
    }

    @Test
    @DisplayName("stealGold: Cobre o caminho de retorno zero, isolando a condição < 0")
    void stealGold_caminhoRetornoZero_quandoQuantiaNegativa() {
        // Cobre o 'else if', focando na condição C2 (< 0). Par com o teste principal.
        double result = creatureCluster.stealGold(-50.0);
        assertEquals(0.0, result);
        assertEquals(1000.0, creatureCluster.getGold());
    }

    // --- TESTES ESTRUTURAIS PARA O MÉTODO loseGold(percentage) ---
    // Decisão 1: if (percentage <= 0 || percentage > 1)
    // Decisão 2: if (this.getGold() <= 0.0)
    @Test
    @DisplayName("loseGold: Cobre o caminho principal onde o ouro é perdido")
    void loseGold_caminhoPrincipal_comOuroEPercentualValidos() {
        // Cobre o 'else' da Decisão 1 e o 'else' da Decisão 2.
        creatureCluster.loseGold(0.5);
        assertEquals(500.0, creatureCluster.getGold());
    }

    @Test
    @DisplayName("loseGold: Cobre o caminho da exceção, isolando a condição <= 0")
    void loseGold_caminhoDeExcecao_quandoPercentualMenorOuIgualAZero() {
        // Cobre o 'if' da Decisão 1, focando na condição C1 (<= 0). Par com o teste principal.
        assertThrows(IllegalArgumentException.class, () -> creatureCluster.loseGold(0.0));
        assertThrows(IllegalArgumentException.class, () -> creatureCluster.loseGold(-0.1));
    }

    @Test
    @DisplayName("loseGold: Cobre o caminho da exceção, isolando a condição > 1")
    void loseGold_caminhoDeExcecao_quandoPercentualMaiorQueUm() {
        // Cobre o 'if' da Decisão 1, focando na condição C2 (> 1). Par com o teste principal.
        assertThrows(IllegalArgumentException.class, () -> creatureCluster.loseGold(1.1));
    }

    @Test
    @DisplayName("loseGold: Cobre o caminho de retorno zero quando o ouro inicial é zero")
    void loseGold_caminhoRetornoZero_quandoOuroInicialEZero() {
        // Cobre o 'if' da Decisão 2.
        creatureCluster.setGold(0.0);
        double result = creatureCluster.loseGold(0.5);
        assertEquals(0.0, result);
        assertEquals(0.0, creatureCluster.getGold());
    }
}
