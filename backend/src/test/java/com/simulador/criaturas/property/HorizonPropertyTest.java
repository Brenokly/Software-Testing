package com.simulador.criaturas.property;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.simulador.criaturas.domain.behaviors.HorizonEntities;
import com.simulador.criaturas.domain.model.CreatureUnit;
import com.simulador.criaturas.domain.model.Guardian;
import com.simulador.criaturas.domain.model.Horizon;
import com.simulador.criaturas.utils.SimulationStatus;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Assume;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.api.constraints.IntRange;

class HorizonPropertyTest {

    @Property
    void constructor_shouldAlwaysInitializeStateCorrectly(
            @ForAll @IntRange(min = 1, max = 100) int numeroDeCriaturas
    ) {
        int idDoGuardiao = numeroDeCriaturas + 1;

        Horizon horizon = new Horizon();
        horizon.initializeEntities(numeroDeCriaturas);
        horizon.setGuardiao(new Guardian(idDoGuardiao));

        assertNotNull(horizon.getEntities());
        assertNotNull(horizon.getGuardiao());
        assertEquals(numeroDeCriaturas, horizon.getEntities().size());
        assertEquals(idDoGuardiao, horizon.getGuardiao().getId());
        assertEquals(SimulationStatus.RUNNING, horizon.getStatus());
    }

    @Property
    void addEntity_shouldAlwaysIncreaseListSizeByOne(
            @ForAll @IntRange(min = 1, max = 100) int numeroInicialDeCriaturas
    ) {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(numeroInicialDeCriaturas);
        horizon.setGuardiao(new Guardian(numeroInicialDeCriaturas + 1));

        int tamanhoInicial = horizon.getEntities().size();
        CreatureUnit novaCriatura = new CreatureUnit(999);

        horizon.addEntity(novaCriatura);

        assertEquals(tamanhoInicial + 1, horizon.getEntities().size());
        assertTrue(horizon.getEntities().contains(novaCriatura));
    }

    @Property
    @Label("Propriedade: Remover uma entidade existente sempre diminui o tamanho da lista em 1")
    void removeEntity_shouldAlwaysDecreaseListSizeByOne(
            @ForAll("listOfUniqueCreatureUnits") List<CreatureUnit> initialCreatures
    ) {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(100));

        horizon.getEntities().clear();
        horizon.getEntities().addAll(initialCreatures);

        int tamanhoInicial = horizon.getEntities().size();
        CreatureUnit entidadeParaRemover = initialCreatures.get(0);

        horizon.removeEntity(entidadeParaRemover);

        assertEquals(tamanhoInicial - 1, horizon.getEntities().size());
        assertFalse(horizon.getEntities().contains(entidadeParaRemover));
    }

    @Provide
    Arbitrary<CreatureUnit> creatureUnits() {
        Arbitrary<Integer> ids = Arbitraries.integers().between(1, 1000);
        return ids.map(id -> new CreatureUnit(id));
    }

    @Provide
    Arbitrary<List<CreatureUnit>> listOfUniqueCreatureUnits() {
        return creatureUnits().list().ofMinSize(1).ofMaxSize(50).uniqueElements();
    }

    @Property
    void getEntitiesInPosition_shouldAlwaysReturnCorrectEntities(
            @ForAll("horizonsWithDefinedPositions") Horizon horizon
    ) {
        double positionToTest = horizon.getEntities().get(0).getX();
        List<HorizonEntities> result = horizon.getEntitiesInPosition(positionToTest);

        List<HorizonEntities> expectedEntities = horizon.getEntities().stream()
                .filter(e -> e.getX() == positionToTest)
                .collect(Collectors.toList());

        assertFalse(result.isEmpty());
        assertTrue(result.containsAll(expectedEntities) && expectedEntities.containsAll(result));
    }

    @Provide
    Arbitrary<Horizon> horizonsWithDefinedPositions() {
        Arbitrary<Integer> size = Arbitraries.integers().between(2, 20);

        return size.map(s -> {
            Horizon horizon = new Horizon();
            horizon.initializeEntities(s);
            horizon.setGuardiao(new Guardian(s + 1));
            for (HorizonEntities entity : horizon.getEntities()) {
                entity.setX(Arbitraries.integers().between(1, 5).sample() * 100.0);
            }
            return horizon;
        });
    }

    @Provide
    Arbitrary<CreatureUnit> fullCreatureUnits() {
        Arbitrary<Integer> id = Arbitraries.integers().between(1, 1000);
        Arbitrary<Double> x = Arbitraries.doubles().between(0, 5000.0);
        Arbitrary<Double> gold = Arbitraries.doubles().between(1, 1000.0);
        return Combinators.combine(id, x, gold).as(CreatureUnit::new);
    }

    @Provide
    Arbitrary<Guardian> fullGuardians() {
        Arbitrary<Integer> id = Arbitraries.integers().between(1001, 2000);
        Arbitrary<Double> x = Arbitraries.doubles().between(0, 5000.0);
        Arbitrary<Double> gold = Arbitraries.doubles().between(1, 1000.0);
        return Combinators.combine(id, x, gold).as(Guardian::new);
    }

    @Provide
    Arbitrary<Horizon> populatedHorizons() {
        Arbitrary<List<CreatureUnit>> creatures = fullCreatureUnits().list().ofSize(5);
        Arbitrary<Guardian> guardian = fullGuardians();

        return Combinators.combine(creatures, guardian).as((creatureList, aGuardian) -> {
            Horizon h = new Horizon();
            creatureList.forEach(h::addEntity);
            h.setGuardiao(aGuardian);
            return h;
        });
    }

    @Property
    void getEntitiesWithinRange_property_shouldOnlyReturnEntitiesWithinRange(
            @ForAll("populatedHorizons") Horizon horizon,
            @ForAll @DoubleRange(min = 0, max = 5000) double centerPosition,
            @ForAll @DoubleRange(min = 0, max = 500) double range
    ) {
        List<HorizonEntities> result = horizon.getEntitiesWithinRange(centerPosition, range);

        for (HorizonEntities foundEntity : result) {
            assertTrue(Math.abs(foundEntity.getX() - centerPosition) <= range);
        }
    }

    @Property
    void getEntitiesWithinRange_property_shouldReturnAllValidEntities(
            @ForAll("populatedHorizons") Horizon horizon,
            @ForAll @DoubleRange(min = 0, max = 5000) double centerPosition,
            @ForAll @DoubleRange(min = 0, max = 500) double range
    ) {
        List<HorizonEntities> allOriginalCreatures = new ArrayList<>(horizon.getEntities());
        List<HorizonEntities> result = horizon.getEntitiesWithinRange(centerPosition, range);

        for (HorizonEntities originalEntity : allOriginalCreatures) {
            boolean isInRange = Math.abs(originalEntity.getX() - centerPosition) <= range;
            if (isInRange) {
                assertTrue(result.contains(originalEntity));
            }
        }
    }

    @Property
    void getEntitiesWithinRange_property_shouldThrowExceptionForInvalidInputs(
            @ForAll("populatedHorizons") Horizon horizon,
            @ForAll double invalidRange
    ) {
        Assume.that(Double.isNaN(invalidRange) || Double.isInfinite(invalidRange) || invalidRange < 0);

        assertThrows(IllegalArgumentException.class, () -> {
            horizon.getEntitiesWithinRange(100.0, invalidRange);
        });
    }
}
