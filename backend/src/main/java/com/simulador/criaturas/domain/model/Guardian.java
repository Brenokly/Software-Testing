package com.simulador.criaturas.domain.model;

import com.simulador.criaturas.domain.behaviors.HorizonEntities;
import com.simulador.criaturas.domain.behaviors.Move;
import com.simulador.criaturas.domain.behaviors.StealGold;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Guardian.java essa classe representa um guardião no jogo.
 * Um guardião é uma criatura que protege um determinado local e pode possuir ouro.
 * Ele é o responsável por derrotar os clusters que se aproximam do seu território.
 * Ele só consegue moedas de ouro se derrotar um cluster.
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Guardian implements HorizonEntities, Move, StealGold {

    private int id;
    private double x;
    private double gold;

    public Guardian(int id) {
        this.id = id;
        this.x = 5_000000.0;
        this.gold = 0.0;
    }
}
