package com.simulador.criaturas.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/*
 * Essa classe representa um grupo de criaturas.
 * Criei ela para facilitar a manipulação das criaturas a cada iteração.
 * Adicionei métodos para gerenciar a lista de criaturas, como adicionar, remover e buscar criaturas.
 */
@Getter
public class Creatures {

    private final List<Creature> creatures; // Lista de criaturas
    private int amountOfCreatures; // Quantidade de criaturas
    private int currentIndex = 0; // Índice da criatura corrente

    public Creatures() {
        this.creatures = new ArrayList<>();
        this.amountOfCreatures = 0;
    }

    public Creatures(int amount) {
        this.creatures = new ArrayList<>();
        this.amountOfCreatures = amount;
        initializeCreatures(amount);
    }

    /**
     * Inicializa a lista de criaturas com base na quantidade especificada.
     *
     * @param amount quantidade de criaturas a serem criadas
     * @return nenhum valor de retorno.
     * @pre amount >= 0
     * @post A lista de criaturas será inicializada com a quantidade
     * especificada.
     * @throws IllegalArgumentException Se a quantidade for menor que zero.
     */
    private void initializeCreatures(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("A quantidade de criaturas não pode ser negativa.");
        }

        for (int i = 0; i < amount; i++) {
            creatures.add(new Creature(i));
        }
    }

    /**
     * Retorna uma criatura com base no seu id.
     *
     * @param id Identificador da criatura.
     * @return A criatura correspondente ao id, ou null se não encontrada.
     * @pre nenhuma pré-condição específica.
     * @post Se retornar não-nulo, a criatura será a que corresponde ao id.
     * @throws Nenhuma exceção é lançada pelo método.
     */
    public Creature getCreature(int id) {
        for (Creature creature : creatures) {
            if (creature.getId() == id) {
                return creature;
            }
        }
        return null;
    }

    /**
     * Retorna a criatura atual (sem avançar).
     *
     * método sem parâmetros.
     *
     * @return A criatura atual, ou null se não houver criaturas.
     * @pre A lista de criaturas não deve estar vazia e a criatura atual não
     * deve ser nula.
     * @post Se retornar não-nulo, a criatura será a atual na lista.
     * @throws IllegalStateException Se a lista de criaturas não estiver vazia e
     * a criatura atual for nula. Antes o índice atual é resetado (0).
     */
    public Creature getCurrent() {
        if (creatures.isEmpty()) {
            return null;
        } else if (creatures.get(currentIndex) == null) {
            // Reseta o índice atual se a criatura atual for nula
            currentIndex = 0;

            throw new IllegalStateException("A criatura atual não pode ser nula.");
        }

        return creatures.get(currentIndex);
    }

    /**
     * Remove uma criatura com base no seu id.
     *
     * @param id Identificador da criatura a ser removida.
     * @return A criatura removida, ou null se não encontrada.
     * @pre A lista de criaturas não deve estar vazia.
     * @post Se retornar não-nulo, a criatura terá sido removida da lista.
     * @throws Nenhuma exceção é lançada pelo método.
     */
    public Creature removeCreature(int id) {
        if (creatures.isEmpty()) {
            return null;
        }

        for (int i = 0; i < creatures.size(); i++) {
            if (creatures.get(i).getId() == id) {
                Creature removed = creatures.remove(i);
                amountOfCreatures = creatures.size();

                if (creatures.isEmpty()) {
                    currentIndex = 0;
                } else if (currentIndex >= creatures.size()) {
                    currentIndex = (currentIndex - 1 + creatures.size()) % creatures.size();
                }

                return removed;
            }
        }
        return null;
    }

    /**
     * Remove a criatura atual da lista de criaturas.
     *
     * método sem parâmetros.
     *
     * @return A criatura removida, ou null se não houver criaturas.
     * @pre A lista de criaturas não deve estar vazia.
     * @post Se retornar não-nulo, a criatura terá sido removida da lista.
     * @throws IllegalStateException Se a lista de criaturas não estiver vazia e
     * a criatura atual for nula.
     */
    public Creature removeCurrent() {
        if (creatures.isEmpty()) {
            return null;
        } else if (creatures.get(currentIndex) == null) {
            throw new IllegalStateException("A criatura atual não pode ser nula.");
        }

        Creature removed = creatures.remove(currentIndex);
        amountOfCreatures = creatures.size();

        if (creatures.isEmpty()) {
            currentIndex = 0;
        } else if (currentIndex >= creatures.size()) {
            currentIndex = (currentIndex - 1 + creatures.size()) % creatures.size();
        }

        return removed;
    }

    /**
     * Retorna a próxima criatura (avança o índice). Caso chegue ao final da
     * lista, retorna a primeira criatura.
     *
     * método sem parâmetros.
     *
     * @return A próxima criatura, ou null se não houver criaturas.
     * @pre nenhuma pré-condição específica.
     * @post Se retornar não-nulo, a criatura será a próxima na lista.
     * @throws Nenhuma exceção é lançada pelo método.
     */
    public Creature next() {
        if (creatures.isEmpty()) {
            return null;
        }
        currentIndex = (currentIndex + 1) % creatures.size();
        if (creatures.get(currentIndex) == null) {
            throw new IllegalStateException("A criatura não pode ser nula.");
        }

        return creatures.get(currentIndex);
    }

    /**
     * Adiciona uma nova criatura à lista de criaturas.
     *
     * @param creature A criatura a ser adicionada.
     * @pre creature != null
     * @post A criatura será adicionada à lista de criaturas e a quantidade será
     * incrementada.
     * @throws IllegalArgumentException Se a criatura for nula.
     */
    public void addCreature(Creature creature) {
        if (creature == null) {
            throw new IllegalArgumentException("A criatura não pode ser nula.");
        }
        creatures.add(creature);
        amountOfCreatures = creatures.size();
    }

    protected void setCurrentIndexForTest(int currentIndex) {
        this.currentIndex = currentIndex;
    }
}
