package com.simulador.criaturas.domain.model;

import com.simulador.criaturas.domain.behaviors.HorizonEntities;
import com.simulador.criaturas.domain.behaviors.LoseGold;
import com.simulador.criaturas.domain.behaviors.Move;
import com.simulador.criaturas.domain.behaviors.StealGold;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Classe que representa uma criatura no simulador.
 * Ela possui um identificador único (id), uma posição no mapa (x) e uma quantidade de ouro (gold).
 * O comportamento de uma criatura é definido por suas interfaces
 * Pois só eles possuem lógica de negócio. O resto é apenas getters e setters.
 *
 * É uma versão pura, apenas java! Faz parte do domínio da aplicação!
 * Como creature é manipulada por outras classes, não faz sentido criar portas de entrada ou saída para ela.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreatureUnit implements HorizonEntities, Move, StealGold, LoseGold {

    private int id;
    private double x;
    private double gold;

    public CreatureUnit(int id) {
        this.id = id;
        this.x = 0.0;
        this.gold = 1_000_000;
    }
}
