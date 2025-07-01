package com.simulador.criaturas.property;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.simulador.criaturas.domain.behaviors.HorizonEntities;
import com.simulador.criaturas.domain.model.CreatureUnit;
import com.simulador.criaturas.domain.model.Guardian;
import com.simulador.criaturas.domain.model.Horizon;
import com.simulador.criaturas.utils.SimulationStatus;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.constraints.IntRange;

class HorizonPropertyTest {

    // --- PROPRIEDADES PARA O CONSTRUTOR ---
    @Property
    void constructorSempreInicializaEstadoCorreto(
            // Gera um número de criaturas válido para o construtor
            @ForAll @IntRange(min = 1, max = 100) int numeroDeCriaturas
    ) {
        // Arrange
        int idDoGuardiao = numeroDeCriaturas + 1;

        // Act
        Horizon horizon = new Horizon();
        horizon.initializeEntities(numeroDeCriaturas);
        horizon.setGuardiao(new Guardian(idDoGuardiao));

        // Assert
        // Propriedades que devem ser sempre verdadeiras após a construção:
        assertNotNull(horizon.getEntities(), "A lista de entidades nunca pode ser nula.");
        assertNotNull(horizon.getGuardiao(), "O guardião nunca pode ser nulo.");
        assertEquals(numeroDeCriaturas, horizon.getEntities().size(), "O número de entidades deve ser o especificado.");
        assertEquals(idDoGuardiao, horizon.getGuardiao().getId(), "O ID do guardião deve ser o especificado.");
        assertEquals(SimulationStatus.RUNNING, horizon.getStatus(), "O status inicial deve ser sempre RUNNING.");
    }

    // --- PROPRIEDADES PARA addEntity ---
    @Property
    void addEntitySempreAumentaTamanhoDaListaEmUm(
            @ForAll @IntRange(min = 1, max = 100) int numeroInicialDeCriaturas
    ) {
        // Arrange
        Horizon horizon = new Horizon();
        horizon.initializeEntities(numeroInicialDeCriaturas);
        horizon.setGuardiao(new Guardian(numeroInicialDeCriaturas + 1));

        int tamanhoInicial = horizon.getEntities().size();
        CreatureUnit novaCriatura = new CreatureUnit(999);

        // Act
        horizon.addEntity(novaCriatura);

        // Assert
        assertEquals(tamanhoInicial + 1, horizon.getEntities().size());
        assertTrue(horizon.getEntities().contains(novaCriatura));
    }

    // --- PROPRIEDADES PARA removeEntity ---
    @Property
    @Label("Propriedade: Remover uma entidade existente sempre diminui o tamanho da lista em 1")
    void removeEntitySempreDiminuiTamanhoDaListaEmUm(
            // CORREÇÃO: Usamos nosso novo provider "listOfUniqueCreatureUnits"
            @ForAll("listOfUniqueCreatureUnits") List<CreatureUnit> initialCreatures
    ) {
        // Arrange
        Horizon horizon = new Horizon(); // Cria um horizonte base
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(100));

        horizon.getEntities().clear();
        horizon.getEntities().addAll(initialCreatures);

        int tamanhoInicial = horizon.getEntities().size();
        CreatureUnit entidadeParaRemover = initialCreatures.get(0);

        // Act
        horizon.removeEntity(entidadeParaRemover);

        // Assert
        assertEquals(tamanhoInicial - 1, horizon.getEntities().size());
        assertFalse(horizon.getEntities().contains(entidadeParaRemover));
    }

    // --- Providers para Geração de Dados Customizados ---
    // Este @Provide ensina o jqwik a criar uma única CreatureUnit
    @Provide
    Arbitrary<CreatureUnit> creatureUnits() {
        // Gera um ID aleatório entre 1 e 1000
        Arbitrary<Integer> ids = Arbitraries.integers().between(1, 1000);
        // Para cada ID gerado, cria uma nova CreatureUnit
        return ids.map(id -> new CreatureUnit(id));
    }

    // Este @Provide usa o anterior para criar uma LISTA de CreatureUnits
    @Provide
    Arbitrary<List<CreatureUnit>> listOfUniqueCreatureUnits() {
        // Pega a nossa "máquina" de criar CreatureUnits...
        // ...e a usa para criar uma lista com tamanho entre 1 e 50...
        // ...garantindo que cada criatura na lista seja única (baseado no equals/hashCode do Lombok).
        return creatureUnits().list().ofMinSize(1).ofMaxSize(50).uniqueElements();
    }

    // --- PROPRIEDADES PARA getEntitiesInPosition ---
    @Property
    void getEntitiesInPositionSempreRetornaEntidadesCorretas(
            @ForAll("horizonsComPosicoesDefinidas") Horizon horizon
    ) {
        // Pega uma posição aleatória que sabemos que existe no nosso horizonte gerado
        double positionToTest = horizon.getEntities().get(0).getX();

        // Act
        List<HorizonEntities> result = horizon.getEntitiesInPosition(positionToTest);

        // Assert
        // Propriedade 1: A lista de resultado não pode ser vazia neste caso
        assertFalse(result.isEmpty());
        // Propriedade 2: TODAS as entidades na lista de resultado DEVEM ter a posição testada
        assertTrue(result.stream().allMatch(e -> e.getX() == positionToTest));
        // Propriedade 3: NENHUMA entidade que ficou de fora pode ter a posição testada
        assertTrue(
                horizon.getEntities().stream()
                        .filter(e -> !result.contains(e))
                        .noneMatch(e -> e.getX() == positionToTest)
        );
    }

    // --- Provider para o Teste Acima ---
    @Provide
    Arbitrary<Horizon> horizonsComPosicoesDefinidas() {
        // Gera um número aleatório de criaturas entre 2 e 20
        Arbitrary<Integer> size = Arbitraries.integers().between(2, 20);

        // Para cada tamanho gerado, cria um Horizonte e define posições X aleatórias para suas criaturas
        return size.map(s -> {
            Horizon horizon = new Horizon();
            horizon.initializeEntities(s);
            horizon.setGuardiao(new Guardian(s + 1));

            // Define posições aleatórias, garantindo algumas colisões
            for (HorizonEntities entity : horizon.getEntities()) {
                entity.setX(Arbitraries.integers().between(1, 5).sample() * 100.0);
            }
            return horizon;
        });
    }
}
