package com.simulador.criaturas.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

public class CreaturesTest {

    // Testes para validar o construtor da classe Creatures

    @Test
    public void creaturesConstructor_givenValidAmount_shouldCreateExpectedNumberOfCreatures() {
        int amount = 5;
        Creatures creatures = new Creatures(amount);
        assertThat(creatures.getCreatures()).hasSize(amount);
    }

    @Test
    public void creaturesConstructor_givenZeroAmount_shouldThrowIllegalArgumentException() {
        int amount = 0;
        Creatures creatures = new Creatures(amount);
        assertThat(creatures.getCreatures()).isEmpty();
        assertThat(creatures.getAmountOfCreatures()).isEqualTo(0);
        assertThat(creatures.getCurrentIndex()).isEqualTo(0);
        assertThat(creatures.getCurrent()).isNull();
        assertThat(creatures.getCreatures()).isEmpty();
        assertThat(creatures.getCreatures()).hasSize(0);
    }

    @Test
    public void creaturesConstructor_givenNegativeAmount_shouldThrowIllegalArgumentException() {
        int amount = -1;
        assertThatThrownBy(() -> new Creatures(amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A quantidade de criaturas não pode ser negativa.");
    }

    // Testes para validar o método getCreature

    @Test
    public void getCreature_givenValidId_shouldReturnCorrectCreature() {
        int amount = 5;
        Creatures creatures = new Creatures(amount);
        Creature creature = creatures.getCreature(2);
        assertThat(creature).isNotNull();
        assertThat(creature.getId()).isEqualTo(2);
    }

    @Test
    public void getCreature_givenInvalidId_shouldReturnNull() {
        int amount = 5;
        Creatures creatures = new Creatures(amount);
        Creature creature = creatures.getCreature(10);
        assertThat(creature).isNull();
    }

    // Testes para validar o método getCurrent

    @Test
    public void getCurrent_givenNonEmptyList_shouldReturnCurrentCreature() {
        int amount = 5;
        Creatures creatures = new Creatures(amount);
        Creature currentCreature = creatures.getCurrent();
        assertThat(currentCreature).isNotNull();
        assertThat(currentCreature.getId()).isEqualTo(0);
    }

    @Test
    public void getCurrent_givenEmptyList_shouldReturnNull() {
        Creatures creatures = new Creatures();
        Creature currentCreature = creatures.getCurrent();
        assertThat(currentCreature).isNull();
    }

    @Test
    public void getCurrent_givenNullCurrentCreature_shouldThrowIllegalStateException() {
        Creatures creatures = new Creatures(3); // cria 3 criaturas válidas

        // Simula manualmente que a criatura atual é nula
        creatures.getCreatures().set(0, null); // posição 0 é a atual (currentIndex = 0)

        assertThatThrownBy(() -> creatures.getCurrent())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("A criatura atual não pode ser nula.");
    }

    // Testes para validar o método removeCreature

    @Test
    public void removeCreature_givenValidId_shouldRemoveAndReturnCreature() {
        int amount = 5;
        Creatures creatures = new Creatures(amount);

        Creature removedCreature = creatures.getCreatures().get(2);
        Creature result = creatures.removeCreature(2);

        assertThat(result).isEqualTo(removedCreature);

        assertThat(creatures.getCreatures()).doesNotContain(removedCreature);

        assertThat(creatures.getCreatures()).hasSize(amount - 1);
    }

    @Test
    public void removeCreature_givenInvalidId_shouldReturnNull() {
        int amount = 5;
        Creatures creatures = new Creatures(amount);
        Creature removedCreature = creatures.removeCreature(10);
        assertThat(removedCreature).isNull();
        assertThat(creatures.getCreatures()).hasSize(amount);
    }

    @Test
    public void removeCreature_givenEmptyList_shouldReturnNull() {
        Creatures creatures = new Creatures();
        Creature removedCreature = creatures.removeCreature(2);
        assertThat(removedCreature).isNull();
        assertThat(creatures.getCreatures()).isEmpty();
    }

    @Test
    public void removeCreature_givenOnlyOneCreature_shouldRemoveAndLeaveListEmpty() {
        Creatures creatures = new Creatures(1);
        Creature removedCreature = creatures.removeCreature(0);
        assertThat(removedCreature).isNotNull();
        assertThat(creatures.getCreatures()).isEmpty();
    }

    @Test
    public void removeCreature_givenLastCreatureAndCurrentIndexOnIt_shouldUpdateCurrentIndexAndReturnCreature() {
        int amount = 4;
        Creatures creatures = new Creatures(amount);

        // Garante que o currentIndex aponte para a última criatura
        creatures.setCurrentIndexForTest(3);

        Creature removedCreature = creatures.getCreatures().get(3);

        Creature result = creatures.removeCreature(3);

        assertThat(result).isEqualTo(removedCreature); // a criatura removida é a esperada
        assertThat(creatures.getCreatures()).doesNotContain(removedCreature); // foi removida mesmo
        assertThat(creatures.getCreatures()).hasSize(amount - 1);

        assertThat(creatures.getCurrentIndex()).isEqualTo(2);

        // Nova criatura atual é a da posição ajustada (2)
        assertThat(creatures.getCurrent()).isEqualTo(creatures.getCreatures().get(2));
    }

    // Testes para validar o método removeCurrent

    @Test
    public void removeCurrent_givenValidState_shouldRemoveCurrentCreature() {
        int amount = 5;
        Creatures creatures = new Creatures(amount);

        Creature removedCreature = creatures.getCreatures().get(0);
        Creature result = creatures.removeCurrent();

        assertThat(result).isEqualTo(removedCreature);

        assertThat(creatures.getCreatures()).doesNotContain(removedCreature);

        assertThat(creatures.getCreatures()).hasSize(amount - 1);

        // Verifica se o currentIndex foi ajustado corretamente
        assertThat(creatures.getCurrentIndex()).isEqualTo(0);
        assertThat(creatures.getCurrent()).isEqualTo(creatures.getCreatures().get(0));
    }

    @Test
    public void removeCurrent_givenEmptyList_shouldReturnNull() {
        Creatures creatures = new Creatures();
        Creature removedCreature = creatures.removeCurrent();
        assertThat(removedCreature).isNull();
        assertThat(creatures.getCreatures()).isEmpty();
    }

    @Test
    public void removeCurrent_givenNullCurrentCreature_shouldThrowIllegalStateException() {
        Creatures creatures = new Creatures(3); // cria 3 criaturas válidas

        // Simula manualmente que a criatura atual é nula
        creatures.getCreatures().set(0, null); // posição 0 é a atual (currentIndex = 0)

        assertThatThrownBy(() -> creatures.removeCurrent())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("A criatura atual não pode ser nula.");
    }

    @Test
    public void removeCurrent_givenOnlyOneCreature_shouldRemoveAndLeaveListEmpty() {
        Creatures creatures = new Creatures(1);
        Creature removedCreature = creatures.removeCurrent();
        assertThat(removedCreature).isNotNull();
        assertThat(creatures.getCreatures()).isEmpty();
    }

    @Test
    public void removeCurrent_givenLastCreature_shouldUpdateCurrentIndex() {
        int amount = 4;
        Creatures creatures = new Creatures(amount);

        // Garante que o currentIndex aponte para a última criatura
        creatures.setCurrentIndexForTest(3);

        Creature removedCreature = creatures.getCreatures().get(3);

        Creature result = creatures.removeCurrent();

        assertThat(result).isEqualTo(removedCreature); // a criatura removida é a esperada
        assertThat(creatures.getCreatures()).doesNotContain(removedCreature); // foi removida mesmo
        assertThat(creatures.getCreatures()).hasSize(amount - 1);

        // Verifica se o currentIndex foi ajustado corretamente
        assertThat(creatures.getCurrentIndex()).isEqualTo(2);
    }

    @Test
    public void removeCurrent_givenLastCreatureAndNullCurrent_shouldThrowIllegalStateException() {
        int amount = 4;
        Creatures creatures = new Creatures(amount);

        // Garante que o currentIndex aponte para a última criatura
        creatures.setCurrentIndexForTest(3);

        // Simula manualmente que a criatura atual é nula
        creatures.getCreatures().set(3, null); // posição 3 é a atual (currentIndex = 3)

        assertThatThrownBy(() -> creatures.removeCurrent())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("A criatura atual não pode ser nula.");
    }

    // Testes para validar o método next

    @Test
    public void next_givenValidState_shouldReturnNextCreature() {
        int amount = 5;
        Creatures creatures = new Creatures(amount);

        Creature currentCreature = creatures.getCurrent();
        Creature nextCreature = creatures.next();

        assertThat(nextCreature).isNotNull();
        assertThat(nextCreature.getId()).isEqualTo((currentCreature.getId() + 1) % amount);
    }

    @Test
    public void next_givenEmptyList_shouldReturnNull() {
        Creatures creatures = new Creatures();
        Creature nextCreature = creatures.next();
        assertThat(nextCreature).isNull();
    }

    @Test
    public void next_givenNullNextCreature_shouldThrowIllegalStateException() {
        Creatures creatures = new Creatures(3); // cria 3 criaturas válidas

        // Simula manualmente que a criatura atual é nula
        creatures.getCreatures().set(1, null); // posição 1 é a atual
        creatures.setCurrentIndexForTest(0);

        assertThatThrownBy(() -> creatures.next())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("A criatura não pode ser nula.");
    }

    // Testes para validar o método addCreature

    @Test
    public void addCreature_givenValidCreature_shouldAddCreatureToList() {
        int amount = 5;
        Creatures creatures = new Creatures(amount);

        Creature newCreature = new Creature(5);
        creatures.addCreature(newCreature);

        assertThat(creatures.getCreatures()).contains(newCreature);
        assertThat(creatures.getCreatures()).hasSize(amount + 1);
    }

    @Test
    public void addCreature_givenNullCreature_shouldThrowIllegalArgumentException() {
        int amount = 5;
        Creatures creatures = new Creatures(amount);

        assertThatThrownBy(() -> creatures.addCreature(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A criatura não pode ser nula.");
    }

}