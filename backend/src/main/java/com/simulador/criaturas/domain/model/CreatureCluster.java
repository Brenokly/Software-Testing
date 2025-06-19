package com.simulador.criaturas.domain.model;

import com.simulador.criaturas.domain.behaviors.Fusion;
import com.simulador.criaturas.domain.behaviors.HorizonEntities;
import com.simulador.criaturas.domain.behaviors.LoseGold;
import com.simulador.criaturas.domain.behaviors.Move;
import com.simulador.criaturas.domain.behaviors.StealGold;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Classe que representa uma cluster de criaturas no simulador.
 * Ela possui um identificador único (id), uma posição no mapa (x) e uma quantidade de ouro (gold).
 * Ela possui comportamentos de movimentação, roubo de ouro, perda de ouro e fusão com outras entidades.
 *
 * É uma versão pura, apenas java! Faz parte do domínio da aplicação!
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreatureCluster implements HorizonEntities, Move, StealGold, LoseGold, Fusion {

    private int id;
    private double x;
    private double gold;

    public CreatureCluster(int id) {
        this.id = id;
        this.x = 0.0;
        this.gold = 0.0;
    }
}
