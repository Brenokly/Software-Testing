package com.simulador.criaturas.structural;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.simulador.criaturas.domain.model.Guardian;

@DisplayName("Testes Estruturais (MC/DC) para os comportamentos de Guardian")
public class GuardianTestF {

    private Guardian guardian;

    @BeforeEach
    public void setUp() {
        guardian = new Guardian(1);
        guardian.setGold(1000.0);
    }

    // --- TESTES ESTRUTURAIS PARA O MÉTODO move(randomR) ---
    // Decisão: if (Double.isNaN(randomR) || randomR < -1 || randomR > 1)
    // Condições: C1: isNaN, C2: < -1, C3: > 1
    @Test
    @DisplayName("move: Cobre o caminho principal (else) onde o movimento ocorre")
    void move_caminhoPrincipal_quandoFatorEValido() {
        // Este teste força C1=false, C2=false, C3=false. A decisão do 'if' é FALSA.
        guardian.setX(100);
        guardian.move(0.5);
        assertEquals(600.0, guardian.getX());
    }

    @Test
    @DisplayName("move: Cobre o caminho da exceção, isolando a condição isNaN (C1)")
    void move_caminhoDeExcecao_quandoFatorIsNaN() {
        // Este teste força C1=true. A decisão do 'if' é VERDADEIRA.
        // Par com o teste acima para MC/DC da condição C1.
        assertThrows(IllegalArgumentException.class, () -> guardian.move(Double.NaN));
    }

    @Test
    @DisplayName("move: Cobre o caminho da exceção, isolando a condição < -1 (C2)")
    void move_caminhoDeExcecao_quandoFatorMenorQueMenosUm() {
        // Este teste força C2=true. A decisão do 'if' é VERDADEIRA.
        // Par com o teste de caminho principal para MC/DC da condição C2.
        assertThrows(IllegalArgumentException.class, () -> guardian.move(-1.1));
    }

    @Test
    @DisplayName("move: Cobre o caminho da exceção, isolando a condição > 1 (C3)")
    void move_caminhoDeExcecao_quandoFatorMaiorQueUm() {
        // Este teste força C3=true. A decisão do 'if' é VERDADEIRA.
        // Par com o teste de caminho principal para MC/DC da condição C3.
        assertThrows(IllegalArgumentException.class, () -> guardian.move(1.1));
    }

    // --- TESTES ESTRUTURAIS PARA O MÉTODO stealGold(amountStolen) ---
    // Decisão 1: if (!Double.isFinite(amountStolen))
    // Decisão 2: else if (amountStolen == 0 || amountStolen < 0)
    @Test
    @DisplayName("stealGold: Cobre o caminho principal (else final) onde o roubo ocorre")
    void stealGold_caminhoPrincipal_quandoQuantiaEValida() {
        // Cobre o 'else' da Decisão 1 e o 'else' da Decisão 2.
        guardian.stealGold(100.0);
        assertEquals(1100.0, guardian.getGold());
    }

    @Test
    @DisplayName("stealGold: Cobre o caminho da exceção para valor não finito")
    void stealGold_caminhoDeExcecao_quandoQuantiaNaoFinita() {
        // Cobre o 'if' da Decisão 1.
        assertThrows(IllegalArgumentException.class, () -> guardian.stealGold(Double.POSITIVE_INFINITY));
    }

    @Test
    @DisplayName("stealGold: Cobre o caminho de retorno zero, isolando a condição == 0")
    void stealGold_caminhoRetornoZero_quandoQuantiaIgualAZero() {
        // Cobre o 'else if', focando na condição C1 (== 0). Par com o teste principal.
        double result = guardian.stealGold(0.0);
        assertEquals(0.0, result);
        assertEquals(1000.0, guardian.getGold()); // Garante que o ouro não mudou
    }

    @Test
    @DisplayName("stealGold: Cobre o caminho de retorno zero, isolando a condição < 0")
    void stealGold_caminhoRetornoZero_quandoQuantiaNegativa() {
        // Cobre o 'else if', focando na condição C2 (< 0). Par com o teste principal.
        double result = guardian.stealGold(-50.0);
        assertEquals(0.0, result);
        assertEquals(1000.0, guardian.getGold());
    }
}
