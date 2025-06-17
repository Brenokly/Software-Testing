package com.simulador.criaturas.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Classe que representa uma cluster de criaturas no simulador.
 * Ela possui um identificador único (id), uma posição no mapa (x) e uma quantidade de ouro (gold).
 * Criei esta classe em vez de usar a própria CreatureUnit para representar um cluster de criaturas.
 * Mas eu poderia usar ela, pois a lógica de negócio, até o momento, é a mesma.
 *
 * É uma versão pura, apenas java! Faz parte do domínio da aplicação!
 * Como creature é manipulada por outras classes, não faz sentido criar portas de entrada ou saída para ela.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatureCluster implements Creature {

    private int id;         // Identificador único do cluster de criaturas
    private double x;       // Posição x da cluster no mapa (Lugar no horizonte)
    private double gold;    // Quantidade de ouro que o cluster possui

    public CreatureCluster(int id) {
        this.id = id;
        this.x = 0.0;
        this.gold = 1_000_000;
    }

    /**
     * Adiciona uma criatura ao cluster, somando o ouro da criatura ao ouro do
     * cluster.
     *
     * @param creature A criatura a ser adicionada ao cluster.
     * @return Nenhum retorno.
     * @throws IllegalArgumentException Se a criatura for nula ou se a
     * quantidade de ouro da criatura não for um número finito.
     * @pre A lista de criaturas não deve estar vazia e a criatura atual não
     * deve ser nula.
     * @post Se retornar não-nulo, a criatura será a atual na lista.
     */
    public void addCreature(Creature creature) {
        if (creature == null) {
            throw new IllegalArgumentException("Criatura não pode ser nula.");
        }

        if (!Double.isFinite(creature.getGold())) {
            throw new IllegalArgumentException("A quantidade de ouro da criatura deve ser um número finito.");
        }

        this.gold += creature.getGold();
    }
}
