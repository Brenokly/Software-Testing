package com.simulador.criaturas.domain.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.simulador.criaturas.domain.behaviors.HorizonEntities;
import com.simulador.criaturas.utils.SimulationStatus;

@DisplayName("Testes de Domínio/Fronteira para a classe Horizon")
class HorizonTestDF {

    // --- CONSTRUTOR ---
    @Test
    @DisplayName("Deve inicializar o Horizonte corretamente com valores válidos")
    void deveInicializarCorretamenteComValoresValidos() {
        int numeroDeCriaturas = 5;
        int idDoGuardiao = 6;

        Horizon horizon = new Horizon();
        horizon.initializeEntities(numeroDeCriaturas);
        horizon.setGuardiao(new Guardian(idDoGuardiao));

        assertNotNull(horizon.getEntities(), "A lista de entidades não deveria ser nula.");
        assertEquals(numeroDeCriaturas, horizon.getEntities().size(), "Deveria ter o número correto de criaturas.");
        assertNotNull(horizon.getGuardiao(), "O guardião não deveria ser nulo.");
        assertEquals(idDoGuardiao, horizon.getGuardiao().getId(), "O ID do guardião deveria ser o correto.");
        assertEquals(SimulationStatus.RUNNING, horizon.getStatus(), "O status inicial deveria ser RUNNING.");
    }

    @Test
    @DisplayName("Deve lançar exceção para número de criaturas igual a zero (Teste de Fronteira)")
    void deveLancarExcecaoParaNumeroDeCriaturasZero() {
        String expectedMessage = "A quantidade de criaturas deve ser positiva.";

        Horizon horizon = new Horizon();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            horizon.initializeEntities(0);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para número de criaturas negativo")
    void deveLancarExcecaoParaNumeroDeCriaturasNegativo() {
        String expectedMessage = "A quantidade de criaturas deve ser positiva.";

        Horizon horizon = new Horizon();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            horizon.initializeEntities(-5);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }

    // --- MÉTODO addEntity ---
    @Test
    @DisplayName("Deve adicionar uma nova entidade com sucesso")
    void deveAdicionarEntidadeComSucesso() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(2);
        horizon.setGuardiao(new Guardian(3));

        CreatureUnit novaCriatura = new CreatureUnit(100);
        int tamanhoInicial = horizon.getEntities().size();

        horizon.addEntity(novaCriatura);

        assertEquals(tamanhoInicial + 1, horizon.getEntities().size());
        assertTrue(horizon.getEntities().contains(novaCriatura));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar adicionar uma entidade nula")
    void deveLancarExcecaoAoAdicionarEntidadeNula() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(2);
        horizon.setGuardiao(new Guardian(3));

        String expectedMessage = "Não é permitido adicionar uma entidade nula ao horizonte.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            horizon.addEntity(null);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }

    // --- MÉTODO removeEntity ---
    @Test
    @DisplayName("Deve remover uma entidade existente com sucesso")
    void deveRemoverEntidadeExistente() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.setGuardiao(new Guardian(4));

        HorizonEntities entidadeParaRemover = horizon.getEntities().get(1);
        int tamanhoInicial = horizon.getEntities().size();

        horizon.removeEntity(entidadeParaRemover);

        assertEquals(tamanhoInicial - 1, horizon.getEntities().size());
        assertFalse(horizon.getEntities().contains(entidadeParaRemover));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover uma entidade nula")
    void deveLancarExcecaoAoRemoverEntidadeNula() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.setGuardiao(new Guardian(4));

        String expectedMessage = "Não é permitido remover uma entidade nula do horizonte.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            horizon.removeEntity(null);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }

    // --- MÉTODO removeEntities ---
    @Test
    @DisplayName("Deve remover uma lista de entidades com sucesso")
    void deveRemoverListaDeEntidadesComSucesso() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(5);
        horizon.setGuardiao(new Guardian(6));

        List<HorizonEntities> aRemover = List.of(horizon.getEntities().get(0), horizon.getEntities().get(2));

        horizon.removeEntities(aRemover);

        assertEquals(3, horizon.getEntities().size());
        assertFalse(horizon.getEntities().containsAll(aRemover));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover uma lista nula")
    void deveLancarExcecaoAoRemoverListaNula() {
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
    @DisplayName("removeEntities: [MC/DC] Cobre o caminho de exceção isolando a condição C1 (lista nula)")
    void removeEntities_caminhoExcecao_quandoListaENula() {
        // Cenário: C1 = true. O resultado da decisão deve ser 'true'.
        // Este teste forma um par com o 'caminhoPrincipal' para validar C1.

        // Arrange
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.setGuardiao(new Guardian(4));

        String expectedMessage = "A lista de entidades a serem removidas não pode ser nula nem conter elementos nulos.";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            horizon.removeEntities(null);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("removeEntities: [MC/DC] Cobre o caminho de exceção isolando a condição C2 (item nulo na lista)")
    void removeEntities_caminhoExcecao_quandoItemNaListaENulo() {
        // Cenário: C1 = false, C2 = true. O resultado da decisão deve ser 'true'.
        // Este teste forma um par com o 'caminhoPrincipal' para validar C2.

        // Arrange
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.setGuardiao(new Guardian(4));

        List<HorizonEntities> listaComNulo = new ArrayList<>();
        listaComNulo.add(horizon.getEntities().get(0));
        listaComNulo.add(null);

        String expectedMessage = "A lista de entidades a serem removidas não pode ser nula nem conter elementos nulos.";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            horizon.removeEntities(listaComNulo);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("removeEntities: [MC/DC] Cobre o caminho principal (else) quando a lista é válida")
    void removeEntities_caminhoPrincipal_quandoListaEValida() {
        // Cenário: C1 = false, C2 = false. O resultado da decisão deve ser 'false'.
        // Este é o teste base que forma par com os outros dois.

        // Arrange
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.setGuardiao(new Guardian(4));

        List<HorizonEntities> listaValida = List.of(horizon.getEntities().get(0), horizon.getEntities().get(2));
        int expectedSize = horizon.getEntities().size() - listaValida.size();

        // Act
        // A asserção aqui é que nenhuma exceção é lançada.
        assertDoesNotThrow(() -> horizon.removeEntities(listaValida));

        // Assert (Verificação do estado final)
        assertEquals(expectedSize, horizon.getEntities().size());
        assertFalse(horizon.getEntities().containsAll(listaValida));
    }

    // --- MÉTODO getEntitiesInPosition ---
    @Test
    @DisplayName("Deve retornar a lista correta de entidades em uma dada posição")
    void deveRetornarEntidadesNaPosicaoCorreta() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.setGuardiao(new Guardian(4));

        horizon.getEntities().get(0).setX(50.0);
        horizon.getEntities().get(1).setX(100.0);
        horizon.getEntities().get(2).setX(50.0);

        List<HorizonEntities> encontradas = horizon.getEntitiesInPosition(50.0);

        assertEquals(2, encontradas.size());
        assertTrue(encontradas.contains(horizon.getEntities().get(0)));
        assertTrue(encontradas.contains(horizon.getEntities().get(2)));
    }

    @Test
    @DisplayName("Deve retornar lista vazia se nenhuma entidade estiver na posição")
    void deveRetornarListaVaziaParaPosicaoSemEntidades() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(3);
        horizon.setGuardiao(new Guardian(4));

        horizon.getEntities().get(0).setX(50.0);

        List<HorizonEntities> encontradas = horizon.getEntitiesInPosition(999.0);

        assertTrue(encontradas.isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção para posição NaN (Teste de Fronteira)")
    void deveLancarExcecaoParaPosicaoNaN() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));

        String expectedMessage = "A posição não pode ser NaN ou Infinita.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            horizon.getEntitiesInPosition(Double.NaN);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para posição Infinita (Teste de Fronteira)")
    void deveLancarExcecaoParaPosicaoInfinita() {
        Horizon horizon = new Horizon();
        horizon.initializeEntities(1);
        horizon.setGuardiao(new Guardian(2));

        String expectedMessage = "A posição não pode ser NaN ou Infinita.";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            horizon.getEntitiesInPosition(Double.POSITIVE_INFINITY);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }
}
