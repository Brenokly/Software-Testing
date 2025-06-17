package com.simulador.criaturas.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Classe que representa uma criatura no simulador.
 * Ela possui um identificador único (id), uma posição no mapa (x) e uma quantidade de ouro (gold).
 * Os únicos métodos que fazem sentido criar testes são os métodos moveCreature, stealGoldFrom e loseGold.
 * Pois só eles possuem lógica de negócio. O resto é apenas getters e setters.
 *
 * É uma versão pura, apenas java! Faz parte do domínio da aplicação!
 * Como creature é manipulada por outras classes, não faz sentido criar portas de entrada ou saída para ela.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatureUnit implements Creature {

    private int id;         // Identificador único da criatura
    private double x;       // Posição x da criatura no mapa (Lugar no horizonte)
    private double gold;    // Quantidade de ouro que a criatura possui

    public CreatureUnit(int id) {
        this.id = id;
        this.x = 0.0;
        this.gold = 1_000_000;
    }
}
