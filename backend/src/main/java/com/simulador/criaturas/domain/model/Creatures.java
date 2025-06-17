package com.simulador.criaturas.domain.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/*
 * Essa classe representa um grupo de criaturas.
 * Criei ela para facilitar a manipulação das criaturas a cada iteração.
 * Adicionei métodos para gerenciar a lista de criaturas, como adicionar, remover e buscar criaturas.
 */
@Data
public class Creatures {

    private final List<Creature> creatures; // Lista de criaturas, que pode conter tanto CreatureUnit quanto CreatureCluster.
    private int amountOfCreatures;          // Quantidade de criaturas no grupo.
    private int currentIndex = 0;           // Índice da criatura atual, usado para iteração atual.

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
     * @throws IllegalArgumentException Se a quantidade for menor que zero.
     * @pre amount >= 0
     * @post A lista de criaturas será inicializada com a quantidade
     * especificada.
     */
    private void initializeCreatures(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("A quantidade de criaturas não pode ser negativa.");
        }
        for (int i = 0; i < amount; i++) {
            creatures.add(new CreatureUnit(i));
        }
    }

    /**
     * Retorna uma criatura com base no seu id.
     *
     * @param id Identificador da criatura.
     * @return A criatura correspondente ao id, ou null se não encontrada.
     * @throws Nenhuma exceção é lançada pelo método.
     * @pre nenhuma pré-condição específica.
     * @post Se retornar não-nulo, a criatura será a que corresponde ao id.
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
     * @return A criatura atual, ou null se não houver criaturas.
     * @throws IllegalStateException Se a lista de criaturas não estiver vazia e
     * a criatura atual for nula. Antes o índice atual é resetado (0).
     * @pre A lista de criaturas não deve estar vazia e a criatura atual não
     * deve ser nula.
     * @post Se retornar não-nulo, a criatura será a atual na lista.
     */
    public Creature getCurrent() {
        if (creatures.isEmpty()) {
            return null;
        }
        // O resto da lógica pode permanecer, pois a lista contém Creatures.
        return creatures.get(currentIndex);
    }

    /**
     * Remove uma criatura com base no seu id.
     *
     * @param id Identificador da criatura a ser removida.
     * @return A criatura removida, ou null se não encontrada.
     * @throws Nenhuma exceção é lançada pelo método.
     * @pre A lista de criaturas não deve estar vazia.
     * @post Se retornar não-nulo, a criatura terá sido removida da lista.
     */
    public Creature removeCreature(int id) {
        // Usamos um Iterator para remover com segurança durante a iteração.
        var iterator = creatures.iterator();
        while (iterator.hasNext()) {
            Creature current = iterator.next(); // <<< MUDANÇA AQUI: Tipo é Creature
            if (current.getId() == id) {
                iterator.remove(); // Remove o elemento da lista
                amountOfCreatures = creatures.size();
                // Lógica para ajustar o currentIndex se necessário...
                return current;
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
     * @throws IllegalStateException Se a lista de criaturas não estiver vazia e
     * a criatura atual for nula.
     * @pre A lista de criaturas não deve estar vazia.
     * @post Se retornar não-nulo, a criatura terá sido removida da lista.
     */
    public Creature removeCurrent() {
        if (creatures.isEmpty()) {
            return null;
        }
        // <<< MUDANÇA AQUI: Tipo é Creature
        Creature removed = creatures.remove(currentIndex);
        amountOfCreatures = creatures.size();
        // Lógica para ajustar o currentIndex...
        if (currentIndex >= creatures.size() && !creatures.isEmpty()) {
            currentIndex = creatures.size() - 1;
        }
        return removed;
    }

    /**
     * Retorna a próxima criatura (avança o índice). Caso chegue ao final da
     * lista, retorna a primeira criatura.
     *
     * @return A próxima criatura, ou null se não houver criaturas.
     * @throws Nenhuma exceção é lançada pelo método.
     * @pre nenhuma pré-condição específica.
     * @post Se retornar não-nulo, a criatura será a próxima na lista.
     */
    public Creature next() {
        if (creatures.isEmpty()) {
            return null;
        }
        currentIndex = (currentIndex + 1) % creatures.size();
        return creatures.get(currentIndex);
    }

    /**
     * Adiciona uma nova criatura à lista de criaturas.
     *
     * @param creature A criatura a ser adicionada.
     * @throws IllegalArgumentException Se a criatura for nula.
     * @pre creature != null
     * @post A criatura será adicionada à lista de criaturas e a quantidade será
     * incrementada.
     */
    public void addCreature(Creature creature) {
        if (creature == null) {
            throw new IllegalArgumentException("Criatura não pode ser nula.");
        }
        creatures.add(creature);
        amountOfCreatures = creatures.size();
    }

    /**
     * Verifica se há colisões em uma determinada posição X. Caso haja, trata as
     * colisões:
     *
     * Se uma colisão ocorrer: 1. Se um dos envolvidos já for um Cluster, ele
     * absorverá todos os outros. 2. Se a colisão for apenas entre Criaturas, um
     * novo Cluster será criado a partir delas.
     *
     * @param position A posição a ser verificada.
     * @pre position != null && !Double.isNaN(position) &&
     * !Double.isInfinite(position)
     * @post Se houver colisões, elas serão tratadas e os clusters serão
     */
    public void handleCollisionsAt(double position) {
        // Verifica se a posição é válida
        if (Double.isNaN(position) || Double.isInfinite(position)) {
            throw new IllegalArgumentException("Posição inválida: " + position);
        }

        // 1. Encontra todos os combatentes na mesma posição
        List<Creature> collided = this.creatures.stream()
                .filter(c -> c.getX() == position)
                .toList();

        // 2. Se houver menos de 2 combatentes, não há colisão. Fim do método.
        if (collided.size() <= 1) {
            return;
        }

        // 3. Procura por um cluster já existente entre os que colidiram
        CreatureCluster baseCluster = collided.stream()
                .filter(c -> c instanceof CreatureCluster)
                .map(c -> (CreatureCluster) c)
                .findFirst()
                .orElse(null); // Retorna o primeiro cluster encontrado, ou null se não houver nenhum

        Creature proxIntCluster;

        //------------------------------------------------------------------
        // CENÁRIO 1: JÁ EXISTE UM CLUSTER NA COLISÃO
        //------------------------------------------------------------------
        if (baseCluster != null) {
            proxIntCluster = baseCluster;

            // Pega todos os outros combatentes que não são o cluster base para serem absorvidos
            List<Creature> toBeAbsorbed = collided.stream()
                    .filter(c -> c != baseCluster)
                    .toList();

            // O cluster base absorve cada um deles
            for (Creature victim : toBeAbsorbed) {
                baseCluster.addCreature(victim);
            }

            // Remove os combatentes que foram absorvidos da lista principal
            this.creatures.removeAll(toBeAbsorbed);

            //------------------------------------------------------------------
            // CENÁRIO 2: NÃO HÁ CLUSTERS, A COLISÃO É SÓ ENTRE CREATUREUNITS
            //------------------------------------------------------------------
        } else {
            // Pega a primeira criatura como base para o novo cluster
            Creature baseCreature = collided.get(0);

            // Cria um novo cluster a partir da primeira criatura
            CreatureCluster newCluster = new CreatureCluster(baseCreature.getId());
            newCluster.setX(baseCreature.getX());
            newCluster.setGold(baseCreature.getGold());

            proxIntCluster = newCluster;

            // Pega todas as outras criaturas para serem absorvidas pelo novo cluster
            List<Creature> toBeAbsorbed = collided.subList(1, collided.size());

            // O novo cluster absorve as outras criaturas
            for (Creature victim : toBeAbsorbed) {
                newCluster.addCreature(victim);
            }

            // Remove TODAS as criaturas originais que colidiram
            this.creatures.removeAll(collided);

            // Adiciona o NOVO e único cluster à lista
            this.creatures.add(newCluster);
        }

        // Atualiza a contagem total de criaturas/clusters
        this.amountOfCreatures = this.creatures.size();

        // Índice atual deve ser ajustado para o novo cluster
        currentIndex = this.creatures.indexOf(proxIntCluster);
    }

    protected void setCurrentIndexForTest(int currentIndex) {
        this.currentIndex = currentIndex;
    }
}
