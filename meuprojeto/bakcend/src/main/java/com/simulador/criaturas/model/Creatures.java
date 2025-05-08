package com.simulador.criaturas.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.ToString;

/*
 * Essa classe representa um grupo de criaturas.
 * Criei ela para facilitar a manipulação das criaturas a cada iteração.
 * Adicionei métodos para gerenciar a lista de criaturas, como adicionar, remover e buscar criaturas.
 */
@Getter
@ToString
public class Creatures {

    private final List<Creature> creatures;    // Lista de criaturas
    private int amoutOfCreatures;              // Quantidade de criaturas
    private int currentIndex = 0;              // Índice da criatura corrente (id da criatura)

    public Creatures(int amoutOfCreatures) {
        this.creatures = new ArrayList<>();
        this.amoutOfCreatures = amoutOfCreatures;
        initializeCreatures();
    }

    private void initializeCreatures() {
        clearCreatures();
        for (int i = 1; i <= amoutOfCreatures; i++) {
            creatures.add(new Creature(i));
        }
    }

    // Pegar uma criatura pelo id
    public Creature getCreature(int id) {
        for (Creature creature : creatures) {
            if (creature.getId() == id) {
                return creature;
            }
        }
        return null;
    }

    public Creature removeCreature(int id) {
        for (int i = 0; i < creatures.size(); i++) {
            if (creatures.get(i).getId() == id) {
                creatures.remove(i);
                amoutOfCreatures--;

                // Corrige o índice atual se necessário
                if (i < currentIndex || currentIndex >= creatures.size()) {
                    currentIndex = (currentIndex - 1 + creatures.size()) % creatures.size();
                }

                return null; // ou retorne a criatura removida, se preferir
            }
        }
        return null;
    }

    // Retorna a criatura atual (sem avançar)
    public Creature getCurrent() {
        if (creatures.isEmpty()) {
            return null;
        }
        return creatures.get(currentIndex);
    }

    // Retorna a próxima criatura (avança o ponteiro de forma circular)
    public Creature next() {
        if (creatures.isEmpty()) {
            return null;
        }
        currentIndex = (currentIndex + 1) % creatures.size();
        return creatures.get(currentIndex);
    }

    // Remove criatura atual e ajusta índice
    public void removeCurrent() {
        if (creatures.isEmpty()) {
            return;
        }
        creatures.remove(currentIndex);
        if (currentIndex >= creatures.size()) {
            currentIndex = 0;
        }
    }

    private void clearCreatures() {
        creatures.clear();
        currentIndex = 0;
    }

    // Reinicia o ponteiro
    public void reset() {
        currentIndex = 0;
    }
}
