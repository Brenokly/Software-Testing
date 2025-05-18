package com.simulador.criaturas.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Creature {

    private int id; // Identificador único da criatura
    private double x; // Posição x da criatura no mapa (Lugar no horizonte)
    private double gold; // Quantidade de ouro que a criatura possui

    public Creature(int id) {
        this.id = id;
        this.x = 0.0;
        this.gold = 1_000_000;
    }

    public void moveCreature(double randomR) {
        this.x += randomR * this.gold; // Move a criatura (X = X + RandomFloat(-1 e 1) * Gold)
    }

    public double giveMoney(double amount) {
        this.gold *= amount; // Adiciona ouro à criatura
        return this.gold; // Retorna o novo valor de ouro
    }
}
