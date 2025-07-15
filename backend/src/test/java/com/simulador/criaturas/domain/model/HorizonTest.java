package com.simulador.criaturas.domain.model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.simulador.criaturas.domain.behaviors.HorizonEntities;
import com.simulador.criaturas.utils.SimulationStatus;

@DisplayName("Testes de Domínio/Fronteira para a classe Horizon")
class HorizonTest {

    @Test
    @DisplayName("Deve inicializar o Horizonte corretamente com valores válidos")
    void initializeEntities_shouldInitializeCorrectly_withValidValues() {
        int numberOfCreatures = 5;
        int guardianId = 6;

        Horizon horizon = new Horizon();
        horizon.initializeEntities(numberOfCreatures);
        horizon.setGuardiao(new Guardian(guardianId));

        assertNotNull(horizon.getEntities());
        assertEquals(numberOfCreatures, horizon.getEntities().size());
        assertNotNull(horizon.getGuardiao());
        assertEquals(guardianId, horizon.getGuardiao().getId());
        assertEquals(SimulationStatus.RUNNING, horizon.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção para número de criaturas igual a zero")
    void initializeEntities_shouldThrowException_forZeroCreatures() {
        String expectedMessage = "A quantidade de criaturas deve ser positiva.";
        Horizon horizon = new Horizon();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            horizon.initializeEntities(0);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para número de criaturas negativo")
    void initializeEntities_shouldThrowException_forNegativeCreatures() {
        String expectedMessage = "A quantidade de criaturas deve ser positiva.";
        Horizon horizon = new Horizon();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            horizon.initializeEntities(-5);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Deve adicionar uma nova entidade com sucesso")
    void addEntity_shouldAddEntitySuccessfully() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(2);
        horizon.setGuardiao(new Guardian(3));

        CreatureUnit newCreature = new CreatureUnit(100);
        int initialSize = horizon.getEntities().size();

        horizon.addEntity(newCreature);

        assertEquals(initialSize + 1, horizon.getEntities().size());
        assertTrue(horizon.getEntities().contains(newCreature));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar adicionar uma entidade nula")
    void addEntity_shouldThrowException_whenEntityIsNull() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(2);
        horizon.setGuardiao(new Guardian(3));
        String expectedMessage = "Não é permitido adicionar uma entidade nula ao horizonte.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            horizon.addEntity(null);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Deve remover uma entidade existente com sucesso")
    void removeEntity_shouldRemoveExistingEntitySuccessfully() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.setGuardiao(new Guardian(4));

        HorizonEntities entityToRemove = horizon.getEntities().get(1);
        int initialSize = horizon.getEntities().size();

        horizon.removeEntity(entityToRemove);

        assertEquals(initialSize - 1, horizon.getEntities().size());
        assertFalse(horizon.getEntities().contains(entityToRemove));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover uma entidade nula")
    void removeEntity_shouldThrowException_whenEntityIsNull() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.setGuardiao(new Guardian(4));
        String expectedMessage = "Não é permitido remover uma entidade nula do horizonte.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            horizon.removeEntity(null);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Deve remover uma lista de entidades com sucesso")
    void removeEntities_shouldRemoveListSuccessfully() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(5);
        horizon.setGuardiao(new Guardian(6));

        List<HorizonEntities> toRemove = List.of(horizon.getEntities().get(0), horizon.getEntities().get(2));

        horizon.removeEntities(toRemove);

        assertEquals(3, horizon.getEntities().size());
        assertFalse(horizon.getEntities().containsAll(toRemove));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover uma lista nula")
    void removeEntities_shouldThrowException_whenListIsNull() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.setGuardiao(new Guardian(4));
        String expectedMessage = "A lista de entidades a serem removidas não pode ser nula nem conter elementos nulos.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            horizon.removeEntities(null);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("removeEntities: [MC/DC] Deve lançar exceção se a lista contiver um item nulo")
    void removeEntities_mcdc_shouldThrowException_whenListContainsNullItem() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.setGuardiao(new Guardian(4));

        List<HorizonEntities> listWithNull = new ArrayList<>();
        listWithNull.add(horizon.getEntities().get(0));
        listWithNull.add(null);

        String expectedMessage = "A lista de entidades a serem removidas não pode ser nula nem conter elementos nulos.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            horizon.removeEntities(listWithNull);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("removeEntities: [MC/DC] Não deve lançar exceção quando a lista é válida")
    void removeEntities_mcdc_shouldNotThrowException_whenListIsValid() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.setGuardiao(new Guardian(4));

        List<HorizonEntities> validList = List.of(horizon.getEntities().get(0), horizon.getEntities().get(2));
        int expectedSize = horizon.getEntities().size() - validList.size();

        assertDoesNotThrow(() -> horizon.removeEntities(validList));

        assertEquals(expectedSize, horizon.getEntities().size());
        assertFalse(horizon.getEntities().containsAll(validList));
    }

    @Test
    @DisplayName("getEntitiesInPosition: Deve retornar a lista correta de entidades em uma dada posição")
    void getEntitiesInPosition_shouldReturnCorrectEntitiesForGivenPosition() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.getEntities().get(0).setX(50.0);
        horizon.getEntities().get(1).setX(100.0);
        horizon.getEntities().get(2).setX(50.0);

        List<HorizonEntities> found = horizon.getEntitiesInPosition(50.0);

        assertEquals(2, found.size());
        assertTrue(found.contains(horizon.getEntities().get(0)));
        assertTrue(found.contains(horizon.getEntities().get(2)));
    }

    @Test
    @DisplayName("getEntitiesInPosition: Deve retornar lista vazia se nenhuma entidade estiver na posição")
    void getEntitiesInPosition_shouldReturnEmptyList_forEmptyPosition() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.getEntities().get(0).setX(50.0);

        List<HorizonEntities> found = horizon.getEntitiesInPosition(999.0);

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("getEntitiesWithinRange: Deve retornar todas as entidades que estão dentro de uma faixa ampla")
    void getEntitiesWithinRange_shouldReturnAllEntities_inWideRange() {
        Horizon horizon = new Horizon();
        CreatureUnit creature1 = new CreatureUnit(1, 10.0, 100);
        CreatureUnit creature2 = new CreatureUnit(2, 20.0, 100);
        Guardian guardian = new Guardian(3, 30.0, 100);
        horizon.addEntity(creature1);
        horizon.addEntity(creature2);
        horizon.setGuardiao(guardian);

        List<HorizonEntities> found = horizon.getEntitiesWithinRange(20.0, 10.0);

        assertEquals(2, found.size()); // Apenas criaturas, o guardião é ignorado
        assertTrue(found.containsAll(List.of(creature1, creature2)));
    }

    @Test
    @DisplayName("getEntitiesWithinRange: Deve retornar apenas entidades específicas dentro de uma faixa estreita")
    void getEntitiesWithinRange_shouldReturnSpecificEntities_inNarrowRange() {
        Horizon horizon = new Horizon();
        CreatureUnit creature1 = new CreatureUnit(1, 10.0, 100);
        CreatureUnit creature2 = new CreatureUnit(2, 20.5, 100);
        CreatureUnit creature3 = new CreatureUnit(3, 30.0, 100);
        horizon.addEntity(creature1);
        horizon.addEntity(creature2);
        horizon.addEntity(creature3);

        List<HorizonEntities> found = horizon.getEntitiesWithinRange(20.0, 0.5);

        assertEquals(1, found.size());
        assertTrue(found.contains(creature2));
    }

    @Test
    @DisplayName("getEntitiesWithinRange: Deve retornar uma lista vazia se nenhuma entidade estiver na faixa")
    void getEntitiesWithinRange_shouldReturnEmptyList_whenNoEntitiesInRange() {
        Horizon horizon = new Horizon();
        horizon.addEntity(new CreatureUnit(1, 10.0, 100));
        horizon.setGuardiao(new Guardian(2, 50.0, 100));

        List<HorizonEntities> found = horizon.getEntitiesWithinRange(100.0, 5.0);

        assertTrue(found.isEmpty());
    }
}
