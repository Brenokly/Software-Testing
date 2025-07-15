package com.simulador.criaturas.structural;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.simulador.criaturas.domain.behaviors.HorizonEntities;
import com.simulador.criaturas.domain.model.CreatureUnit;
import com.simulador.criaturas.domain.model.Guardian;
import com.simulador.criaturas.domain.model.Horizon;
import com.simulador.criaturas.utils.SimulationStatus;

@DisplayName("Testes Estruturais (MC/DC) para os comportamentos de Horizon")
public class HorizonStructuralTest {

    @Test
    @DisplayName("Construtor: Cobre o caminho principal (else) com quantidade válida")
    void initializeEntities_shouldFollowHappyPath_withValidAmount() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(5);
        horizon.setGuardiao(new Guardian(6));

        assertEquals(5, horizon.getEntities().size());
        assertEquals(SimulationStatus.RUNNING, horizon.getStatus());
    }

    @Test
    @DisplayName("Construtor: Cobre o caminho de exceção (if) com quantidade zero")
    void initializeEntities_shouldFollowExceptionPath_withZeroAmount() {
        Horizon horizon = new Horizon();
        horizon.setGuardiao(new Guardian(6));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> horizon.initializeEntities(0));
        assertEquals("A quantidade de criaturas deve ser positiva.", exception.getMessage());
    }

    @Test
    @DisplayName("addEntity: Cobre o caminho principal com entidade válida")
    void addEntity_shouldFollowHappyPath_withValidEntity() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));

        horizon.addEntity(new CreatureUnit(10));
        assertEquals(2, horizon.getEntities().size());
    }

    @Test
    @DisplayName("addEntity: Cobre o caminho de exceção com entidade nula")
    void addEntity_shouldFollowExceptionPath_withNullEntity() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> horizon.addEntity(null));
        assertEquals("Não é permitido adicionar uma entidade nula ao horizonte.", exception.getMessage());
    }

    @Test
    @DisplayName("removeEntity: Cobre o caminho principal com entidade válida")
    void removeEntity_shouldFollowHappyPath_withValidEntity() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.setGuardiao(new Guardian(4));

        HorizonEntities entityToRemove = horizon.getEntities().get(0);

        assertDoesNotThrow(() -> horizon.removeEntity(entityToRemove));
        assertEquals(2, horizon.getEntities().size());
    }

    @Test
    @DisplayName("removeEntity: Cobre o caminho de exceção com entidade nula")
    void removeEntity_shouldFollowExceptionPath_withNullEntity() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.setGuardiao(new Guardian(4));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> horizon.removeEntity(null));
        assertEquals("Não é permitido remover uma entidade nula do horizonte.", exception.getMessage());
    }

    @Test
    @DisplayName("removeEntities: Cobre o caminho principal com lista válida")
    void removeEntities_shouldFollowHappyPath_withValidList() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.setGuardiao(new Guardian(4));

        List<HorizonEntities> toRemove = List.of(horizon.getEntities().get(0));

        assertDoesNotThrow(() -> horizon.removeEntities(toRemove));
        assertEquals(2, horizon.getEntities().size());
    }

    @Test
    @DisplayName("removeEntities: Cobre o caminho de exceção, isolando a condição C1 (lista nula)")
    void removeEntities_mcdc_shouldFollowExceptionPath_whenListIsNull() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.setGuardiao(new Guardian(4));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> horizon.removeEntities(null));
        assertEquals("A lista de entidades a serem removidas não pode ser nula nem conter elementos nulos.", exception.getMessage());
    }

    @Test
    @DisplayName("removeEntities: Cobre o caminho de exceção, isolando a condição C2 (item nulo na lista)")
    void removeEntities_mcdc_shouldFollowExceptionPath_whenListItemIsNull() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.setGuardiao(new Guardian(4));

        List<HorizonEntities> toRemove = new ArrayList<>();
        toRemove.add(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> horizon.removeEntities(toRemove));
        assertEquals("A lista de entidades a serem removidas não pode ser nula nem conter elementos nulos.", exception.getMessage());
    }

    @Test
    @DisplayName("getEntitiesInPosition: Cobre o caminho principal com posição válida")
    void getEntitiesInPosition_shouldFollowHappyPath_withValidPosition() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.getEntities().get(0).setX(100.0);

        List<HorizonEntities> result = horizon.getEntitiesInPosition(100.0);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getEntitiesInPosition: [MC/DC] Deve retornar entidades com posição válida")
    void getEntitiesInPosition_shouldFollowHappyPath_withMultipleEntitiesAtValidPosition() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(2);
        horizon.getEntities().get(0).setX(100.0);
        horizon.getEntities().get(1).setX(100.0);

        assertEquals(2, horizon.getEntitiesInPosition(100.0).size());
    }

    @Test
    @DisplayName("getEntitiesInPosition: Cobre o caminho de exceção, isolando a condição C1 (isNaN)")
    void getEntitiesInPosition_mcdc_shouldFollowExceptionPath_whenPositionIsNaN() {
        Horizon horizon = new Horizon();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> horizon.getEntitiesInPosition(Double.NaN));
        assertEquals("A posição não pode ser NaN ou Infinita.", exception.getMessage());
    }

    @Test
    @DisplayName("getEntitiesInPosition: Cobre o caminho de exceção, isolando a condição C2 (isInfinite)")
    void getEntitiesInPosition_mcdc_shouldFollowExceptionPath_whenPositionIsInfinite() {
        Horizon horizon = new Horizon();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> horizon.getEntitiesInPosition(Double.POSITIVE_INFINITY));
        assertEquals("A posição não pode ser NaN ou Infinita.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve incluir uma entidade que está exatamente na borda da faixa")
    void getEntitiesWithinRange_shouldIncludeEntity_onExactBoundary() {
        Horizon horizon = new Horizon();
        CreatureUnit creatureOnEdge = new CreatureUnit(1, 10.0, 100);
        horizon.addEntity(creatureOnEdge);

        List<HorizonEntities> found = horizon.getEntitiesWithinRange(15.0, 5.0);

        assertTrue(found.contains(creatureOnEdge));
        assertEquals(1, found.size());
    }

    @Test
    @DisplayName("Não deve incluir uma entidade que está logo fora da borda da faixa")
    void getEntitiesWithinRange_shouldNotIncludeEntity_outsideBoundary() {
        Horizon horizon = new Horizon();
        CreatureUnit creatureOutside = new CreatureUnit(1, 9.99, 100);
        horizon.addEntity(creatureOutside);

        List<HorizonEntities> found = horizon.getEntitiesWithinRange(15.0, 5.0);

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Deve funcionar como busca exata quando a faixa (range) é 0")
    void getEntitiesWithinRange_shouldPerformExactSearch_whenRangeIsZero() {
        Horizon horizon = new Horizon();
        CreatureUnit targetCreature = new CreatureUnit(1, 10.0, 100);
        horizon.addEntity(targetCreature);
        horizon.addEntity(new CreatureUnit(2, 10.001, 100));

        List<HorizonEntities> found = horizon.getEntitiesWithinRange(10.0, 0.0);

        assertEquals(1, found.size());
        assertTrue(found.contains(targetCreature));
    }

    @Test
    @DisplayName("Deve lançar exceção para centerPosition negativo")
    void getEntitiesWithinRange_shouldThrowException_forNegativeCenterPosition() {
        Horizon horizon = new Horizon();
        assertThrows(IllegalArgumentException.class, () -> horizon.getEntitiesWithinRange(-10.0, 5.0));
    }

    @Test
    @DisplayName("Deve lançar exceção para range negativo")
    void getEntitiesWithinRange_shouldThrowException_forNegativeRange() {
        Horizon horizon = new Horizon();
        assertThrows(IllegalArgumentException.class, () -> horizon.getEntitiesWithinRange(10.0, -5.0));
    }

    @Test
    @DisplayName("Deve lançar exceção para centerPosition sendo NaN")
    void getEntitiesWithinRange_shouldThrowException_forNaNCenterPosition() {
        Horizon horizon = new Horizon();
        assertThrows(IllegalArgumentException.class, () -> horizon.getEntitiesWithinRange(Double.NaN, 5.0));
    }

    @Test
    @DisplayName("Não deve modificar a lista original de entidades do horizonte")
    void getEntitiesWithinRange_shouldNotModifyOriginalList() {
        Horizon horizon = new Horizon();
        CreatureUnit c1 = new CreatureUnit(1, 10.0, 100);
        CreatureUnit c2 = new CreatureUnit(2, 20.0, 100);
        horizon.addEntity(c1);
        horizon.addEntity(c2);

        int originalSize = horizon.getEntities().size();
        List<HorizonEntities> originalListCopy = new ArrayList<>(horizon.getEntities());

        horizon.getEntitiesWithinRange(15.0, 10.0);

        assertEquals(originalSize, horizon.getEntities().size());
        assertEquals(originalListCopy, horizon.getEntities());
    }

    @Test
    @DisplayName("Não deve adicionar o guardião duas vezes se ele já estiver na lista")
    void getEntitiesWithinRange_shouldNotAddGuardianTwice() {
        Horizon horizon = new Horizon();
        Guardian guardian = new Guardian(1, 40.0, 500);
        horizon.setGuardiao(guardian);
        horizon.addEntity(guardian);

        List<HorizonEntities> found = horizon.getEntitiesWithinRange(40.0, 1.0);

        assertEquals(1, found.size());
    }
}
