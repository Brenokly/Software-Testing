package com.simulador.criaturas.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Classe que representa uma criatura no simulador.
 * Ela possui um identificador único (id), uma posição no mapa (x) e uma quantidade de ouro (gold).
 * Os únicos métodos que fazem sentido criar testes são os métodos moveCreature, stealGoldFrom e loseGold.
 * Pois só eles possuem lógica de negócio. O resto é apenas getters e setters.
*/

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

    /**
     * Move a criatura com base em um fator aleatório e na quantidade de ouro.
     *
     * @param randomR Valor aleatório no intervalo [-1, 1].
     * @return Nada.
     * @pre -1 <= randomR <= 1
     * @post x será atualizado com x + (randomR * gold).
     * @throws IllegalArgumentException se randomR estiver fora do intervalo [-1,
     *                                  1].
     */
    public void moveCreature(double randomR) {
        if (randomR < -1 || randomR > 1) {
            throw new IllegalArgumentException("Valor aleatório deve estar entre -1 e 1.");
        }

        this.x += randomR * this.gold; // Move a criatura (X = X + RandomFloat(-1 e 1) * Gold)
    }

    /**
     * Rouba ouro de outra criatura.
     *
     * @param victim     Criatura alvo do roubo.
     * @param percentage Percentual do ouro a ser roubado (entre 0 e 1, exclusivo de
     *                   0).
     * @return Quantidade de ouro roubada da vítima.
     * @pre victim != null && 0 < percentage <= 1
     * @post gold += ouro roubado, se finito e positivo.
     * @throws IllegalArgumentException se precondições forem violadas.
     */
    public double stealGoldFrom(Creature victim, double percentage) {
        if (victim == null) {
            throw new IllegalArgumentException("Vítima não pode ser nula.");
        }

        if (percentage <= 0 || percentage > 1) {
            throw new IllegalArgumentException("Percentual de roubo deve estar entre 0 e 1 (exclusivo de 0).");
        }

        double amountStolen = victim.loseGold(percentage);

        // Evita NaN ou infinitos causados por somas com valores inválidos
        if (Double.isFinite(amountStolen) && amountStolen > 0) {
            this.gold += amountStolen;
        }

        return amountStolen;
    }

    /**
     * Reduz a quantidade de ouro com base em um percentual.
     *
     * @param percentage Percentual a ser perdido (entre 0 e 1, exclusivo de 0).
     * @return Quantidade de ouro efetivamente perdida.
     * @pre 0 < percentage <= 1
     * @post gold -= (gold * percentage), garantindo que gold >= 0.
     * @throws IllegalArgumentException se percentage for inválido.
     */
    public double loseGold(double percentage) {
        if (percentage <= 0 || percentage > 1) {
            throw new IllegalArgumentException("Percentual de perda deve estar entre 0 e 1 (exclusivo de 0).");
        }

        if (this.gold <= 0) {
            return 0; // Nada a perder
        }

        double amountLost = this.gold * percentage;
        this.gold -= amountLost;

        // Garante que o ouro nunca fique negativo por erro de precisão
        if (this.gold < 0) {
            this.gold = 0;
        }

        return amountLost;
    }

}