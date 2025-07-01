package com.simulador.criaturas.structural;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.simulador.criaturas.domain.behaviors.HorizonEntities;
import com.simulador.criaturas.domain.model.CreatureUnit;
import com.simulador.criaturas.domain.model.Guardian;
import com.simulador.criaturas.domain.model.Horizon;
import com.simulador.criaturas.utils.SimulationStatus;

@DisplayName("Testes Estruturais (MC/DC) para os comportamentos de Horizon")
public class HorizonTestS {

// --- TESTES ESTRUTURAIS PARA O CONSTRUTOR (via initializeEntities) ---
    // Decisão: if (amount <= 0)
    @Test
    @DisplayName("Construtor: Cobre o caminho principal (else) com quantidade válida")
    void constructor_caminhoPrincipal_comQuantidadeValida() {
        // Força a condição 'amount <= 0' a ser FALSA
        Horizon horizon = new Horizon();
        horizon.initializeEntities(5);
        horizon.setGuardiao(new Guardian(6));

        assertEquals(5, horizon.getEntities().size());
        assertEquals(SimulationStatus.RUNNING, horizon.getStatus());
    }

    @Test
    @DisplayName("Construtor: Cobre o caminho de exceção (if) com quantidade zero")
    void constructor_caminhoDeExcecao_comQuantidadeZero() {
        // Força a condição 'amount <= 0' a ser VERDADEIRA

        Horizon horizon = new Horizon();
        horizon.setGuardiao(new Guardian(6));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> horizon.initializeEntities(0));
        assertEquals("A quantidade de criaturas deve ser positiva.", exception.getMessage());
    }

    // --- TESTES ESTRUTURAIS PARA addEntity(entity) ---
    // Decisão: if (entity == null)
    @Test
    @DisplayName("addEntity: Cobre o caminho principal com entidade válida")
    void addEntity_caminhoPrincipal_comEntidadeValida() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));

        horizon.addEntity(new CreatureUnit(10));
        assertEquals(2, horizon.getEntities().size());
    }

    @Test
    @DisplayName("addEntity: Cobre o caminho de exceção com entidade nula")
    void addEntity_caminhoDeExcecao_comEntidadeNula() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> horizon.addEntity(null));
        assertEquals("Não é permitido adicionar uma entidade nula ao horizonte.", exception.getMessage());
    }

    // --- TESTES ESTRUTURAIS PARA removeEntity(entity) ---
    // Decisão: if (entity == null)
    // Condições: C1: entity == null, C2: entity != null
    @Test
    @DisplayName("removeEntity: Cobre o caminho principal com entidade válida")
    void removeEntity_caminhoPrincipal_comEntidadeValida() {
        // Força C1=false. A decisão do 'if' é FALSA.
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.setGuardiao(new Guardian(4));

        HorizonEntities entityToRemove = horizon.getEntities().get(0);

        assertDoesNotThrow(() -> horizon.removeEntity(entityToRemove));
        assertEquals(2, horizon.getEntities().size());
    }

    @Test
    @DisplayName("removeEntity: Cobre o caminho de exceção com entidade nula")
    void removeEntity_caminhoDeExcecao_comEntidadeNula() {
        // Força C1=true. A decisão do 'if' é VERDADEIRA.
        // Par com o teste principal para MC/DC da condição C1.
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.setGuardiao(new Guardian(4));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> horizon.removeEntity(null));
        assertEquals("Não é permitido remover uma entidade nula do horizonte.", exception.getMessage());
    }

    // --- TESTES ESTRUTURAIS PARA removeEntities(toRemove) ---
    // Decisão: if (toRemove == null || toRemove.stream().anyMatch(e -> e == null))
    // Condições: C1: toRemove == null, C2: anyMatch(e -> e == null)
    @Test
    @DisplayName("removeEntities: Cobre o caminho principal com lista válida")
    void removeEntities_caminhoPrincipal_comListaValida() {
        // Força C1=false, C2=false. A decisão do 'if' é FALSA.
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.setGuardiao(new Guardian(4));

        List<HorizonEntities> toRemove = List.of(horizon.getEntities().get(0));

        assertDoesNotThrow(() -> horizon.removeEntities(toRemove));
        assertEquals(2, horizon.getEntities().size());
    }

    @Test
    @DisplayName("removeEntities: Cobre o caminho de exceção, isolando a condição C1 (lista nula)")
    void removeEntities_caminhoDeExcecao_quandoListaENula() {
        // Força C1=true. A decisão do 'if' é VERDADEIRA.
        // Par com o teste principal para MC/DC da condição C1.
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.setGuardiao(new Guardian(4));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> horizon.removeEntities(null));
        assertEquals("A lista de entidades a serem removidas não pode ser nula nem conter elementos nulos.", exception.getMessage());
    }

    @Test
    @DisplayName("removeEntities: Cobre o caminho de exceção, isolando a condição C2 (item nulo na lista)")
    void removeEntities_caminhoDeExcecao_quandoItemNaListaENulo() {
        // Força C1=false, C2=true. A decisão do 'if' é VERDADEIRA.
        // Par com o teste principal para MC/DC da condição C2.
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.setGuardiao(new Guardian(4));

        List<HorizonEntities> toRemove = new ArrayList<>();
        toRemove.add(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> horizon.removeEntities(toRemove));
        assertEquals("A lista de entidades a serem removidas não pode ser nula nem conter elementos nulos.", exception.getMessage());
    }

    // --- TESTES ESTRUTURAIS PARA getEntitiesInPosition(position) ---
    // Decisão: if (Double.isNaN(position) || Double.isInfinite(position))
    // Condições: C1: isNaN, C2: isInfinite
    @Test
    @DisplayName("getEntitiesInPosition: Cobre o caminho principal com posição válida")
    void getEntitiesInPosition_caminhoPrincipal_comPosicaoValida() {
        // Força C1=false, C2=false. A decisão do 'if' é FALSA.
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));

        horizon.getEntities().get(0).setX(100.0);

        List<HorizonEntities> result = horizon.getEntitiesInPosition(100.0);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getEntitiesInPosition: Cobre o caminho de exceção, isolando a condição C1 (isNaN)")
    void getEntitiesInPosition_caminhoDeExcecao_quandoPosicaoIsNaN() {
        // Força C1=true. A decisão do 'if' é VERDADEIRA.
        // Par com o teste principal para MC/DC da condição C1.
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> horizon.getEntitiesInPosition(Double.NaN));
        assertEquals("A posição não pode ser NaN ou Infinita.", exception.getMessage());
    }

    @Test
    @DisplayName("getEntitiesInPosition: Cobre o caminho de exceção, isolando a condição C2 (isInfinite)")
    void getEntitiesInPosition_caminhoDeExcecao_quandoPosicaoIsInfinite() {
        // Força C1=false, C2=true. A decisão do 'if' é VERDADEIRA.
        // Par com o teste principal para MC/DC da condição C2.
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> horizon.getEntitiesInPosition(Double.POSITIVE_INFINITY));
        assertEquals("A posição não pode ser NaN ou Infinita.", exception.getMessage());
    }
}
