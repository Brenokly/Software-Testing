package com.simulador.criaturas.structural;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.simulador.criaturas.domain.model.CreatureUnit;

@DisplayName("Testes Estruturais (MC/DC) para os comportamentos de CreatureUnit")
public class CreatureUnitTestS {

    private CreatureUnit creature;

    @BeforeEach
    void setUp() {
        creature = new CreatureUnit(1);
        creature.setGold(1000.0);
    }

    // --- TESTES ESTRUTURAIS PARA O MÉTODO move(randomR) ---
    // Decisão: if (Double.isNaN(randomR) || randomR < -1 || randomR > 1)
    // Condições: C1: isNaN, C2: < -1, C3: > 1
    @Test
    @DisplayName("move: Cobre o caminho principal (else) onde o movimento ocorre")
    void move_caminhoPrincipal_quandoFatorEValido() {
        // Este teste força C1=false, C2=false, C3=false. A decisão do 'if' é FALSA.
        creature.setX(100);
        creature.move(0.5);
        assertEquals(600.0, creature.getX());
    }

    @Test
    @DisplayName("move: Cobre o caminho da exceção, isolando a condição isNaN (C1)")
    void move_caminhoDeExcecao_quandoFatorIsNaN() {
        // Este teste força C1=true. A decisão do 'if' é VERDADEIRA.
        // Par com o teste acima para MC/DC da condição C1.
        assertThrows(IllegalArgumentException.class, () -> creature.move(Double.NaN));
    }

    @Test
    @DisplayName("move: Cobre o caminho da exceção, isolando a condição < -1 (C2)")
    void move_caminhoDeExcecao_quandoFatorMenorQueMenosUm() {
        // Este teste força C2=true. A decisão do 'if' é VERDADEIRA.
        // Par com o teste de caminho principal para MC/DC da condição C2.
        assertThrows(IllegalArgumentException.class, () -> creature.move(-1.1));
    }

    @Test
    @DisplayName("move: Cobre o caminho da exceção, isolando a condição > 1 (C3)")
    void move_caminhoDeExcecao_quandoFatorMaiorQueUm() {
        // Este teste força C3=true. A decisão do 'if' é VERDADEIRA.
        // Par com o teste de caminho principal para MC/DC da condição C3.
        assertThrows(IllegalArgumentException.class, () -> creature.move(1.1));
    }

    // --- TESTES ESTRUTURAIS PARA O MÉTODO stealGold(amountStolen) ---
    // Decisão 1: if (!Double.isFinite(amountStolen))
    // Decisão 2: else if (amountStolen == 0 || amountStolen < 0)
    @Test
    @DisplayName("stealGold: Cobre o caminho principal (else final) onde o roubo ocorre")
    void stealGold_caminhoPrincipal_quandoQuantiaEValida() {
        // Cobre o 'else' da Decisão 1 e o 'else' da Decisão 2.
        creature.stealGold(100.0);
        assertEquals(1100.0, creature.getGold());
    }

    @Test
    @DisplayName("stealGold: Cobre o caminho da exceção para valor não finito")
    void stealGold_caminhoDeExcecao_quandoQuantiaNaoFinita() {
        // Cobre o 'if' da Decisão 1.
        assertThrows(IllegalArgumentException.class, () -> creature.stealGold(Double.POSITIVE_INFINITY));
    }

    @Test
    @DisplayName("stealGold: Cobre o caminho de retorno zero, isolando a condição == 0")
    void stealGold_caminhoRetornoZero_quandoQuantiaIgualAZero() {
        // Cobre o 'else if', focando na condição C1 (== 0). Par com o teste principal.
        double result = creature.stealGold(0.0);
        assertEquals(0.0, result);
        assertEquals(1000.0, creature.getGold()); // Garante que o ouro não mudou
    }

    @Test
    @DisplayName("stealGold: Cobre o caminho de retorno zero, isolando a condição < 0")
    void stealGold_caminhoRetornoZero_quandoQuantiaNegativa() {
        // Cobre o 'else if', focando na condição C2 (< 0). Par com o teste principal.
        double result = creature.stealGold(-50.0);
        assertEquals(0.0, result);
        assertEquals(1000.0, creature.getGold());
    }

    // --- TESTES ESTRUTURAIS PARA O MÉTODO loseGold(percentage) ---
    // Decisão 1: if (percentage <= 0 || percentage > 1)
    // Decisão 2: if (this.getGold() <= 0.0)
    @Test
    @DisplayName("loseGold: Cobre o caminho principal onde o ouro é perdido")
    void loseGold_caminhoPrincipal_comOuroEPercentualValidos() {
        // Cobre o 'else' da Decisão 1 e o 'else' da Decisão 2.
        creature.loseGold(0.5);
        assertEquals(500.0, creature.getGold());
    }

    @Test
    @DisplayName("loseGold: Cobre o caminho da exceção, isolando a condição <= 0")
    void loseGold_caminhoDeExcecao_quandoPercentualMenorOuIgualAZero() {
        // Cobre o 'if' da Decisão 1, focando na condição C1 (<= 0). Par com o teste principal.
        assertThrows(IllegalArgumentException.class, () -> creature.loseGold(0.0));
        assertThrows(IllegalArgumentException.class, () -> creature.loseGold(-0.1));
    }

    @Test
    @DisplayName("loseGold: Cobre o caminho da exceção, isolando a condição > 1")
    void loseGold_caminhoDeExcecao_quandoPercentualMaiorQueUm() {
        // Cobre o 'if' da Decisão 1, focando na condição C2 (> 1). Par com o teste principal.
        assertThrows(IllegalArgumentException.class, () -> creature.loseGold(1.1));
    }

    @Test
    @DisplayName("loseGold: Cobre o caminho de retorno zero quando o ouro inicial é zero")
    void loseGold_caminhoRetornoZero_quandoOuroInicialEZero() {
        // Cobre o 'if' da Decisão 2.
        creature.setGold(0.0);
        double result = creature.loseGold(0.5);
        assertEquals(0.0, result);
        assertEquals(0.0, creature.getGold());
    }
}
