package com.simulador.criaturas.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

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
@Data
public class Creature {

    private int id;         // Identificador único da criatura
    private double x;       // Posição x da criatura no mapa (Lugar no horizonte)
    private double gold;    // Quantidade de ouro que a criatura possui

    public Creature(int id) {
        this.id = id;
        this.x = 0.0;
        this.gold = 1_000_000;
    }

    /**
     * Move a criatura com base em um fator aleatório e na quantidade de ouro.
     *
     * @param randomR Valor aleatório no intervalo [-1, 1].
     * @return Nenhum retorno.
     * @throws IllegalArgumentException se randomR estiver fora do intervalo
     *                                  [-1, 1] ou se randomR for NaN ou infinito.
     * @pre -1 <= randomR <= 1.
     * @post X será atualizado com X + (randomR * Gold).
     */
    public void moveCreature(double randomR) {
        // Domínio: -1 <= randomR <= 1, Fronteira: -1 <= randomR <= 1
        // Partições: Invalid [-infinity, -1), Valid [-1, 1], Invalid (1, +infinity]
        if (Double.isNaN(randomR) || randomR < -1 || randomR > 1) {
            throw new IllegalArgumentException("Valor aleatório deve estar entre -1 e 1.");
        }

        this.x += randomR * this.gold;
    }

    /**
     * Rouba ouro de outra criatura.
     *
     * @param victim     Criatura alvo do roubo.
     * @param percentage Percentual do ouro a ser roubado (onde 0 < percentage
     *                   <= 1).
     * @return Quantidade de ouro roubada da vítima.
     * @throws IllegalArgumentException se precondições ou poscondições forem
     *                                  violadas.
     * @pre victim != null && 0 < percentage <= 1
     * @post Ouro da vítima é reduzido em percentage e o ouro do ladrão é
     * aumentado.
     */
    public double stealGoldFrom(Creature victim, double percentage) {
        // Domínio: victim != null && 0 < percentage <= 1, Fronteira: victim != null &&
        // 0 < percentage <= 1
        // Partições:
        // Creature: Invalid [null], Valid [!= null]
        // percentage: Invalid [-infinity, 0], Valid (0 < percentage <= 1] e Invalid (1,
        // +infinity]
        if (victim == null) {
            throw new IllegalArgumentException("Vítima não pode ser nula.");
        }

        if (percentage <= 0 || percentage > 1) {
            throw new IllegalArgumentException("Percentual de roubo deve estar entre 0 e 1 (exclusivo de 0).");
        }

        double amountStolen = victim.loseGold(percentage);

        // Evita NaN ou infinitos causados por somas com valores inválidos
        if (!Double.isFinite(amountStolen) || amountStolen < 0) {
            throw new IllegalArgumentException("Roubo inválido: valor roubado não pode ser negativo ou infinito.");
        } else if (amountStolen == 0) {
            return 0; // Nada a roubar
        }

        // Neste ponto, amountStolen é garantidamente > 0 e finito
        this.gold += amountStolen;

        return amountStolen;
    }

    /**
     * Reduz a quantidade de ouro com base em um percentual.
     *
     * @param percentage Percentual a ser perdido (onde 0 < percentage <= 1).
     * @return Quantidade de ouro efetivamente perdida.
     * @throws IllegalArgumentException se percentage for inválido.
     * @pre 0 < percentage <= 1
     * @post gold -= (gold * percentage), garantindo que gold >= 0.
     */
    public double loseGold(double percentage) {
        // Domínio: 0 < percentage <= 1, Fronteira: 0 < percentage <= 1
        // Partições: Invalid [-infinity, 0], Valid (0 < percentage <= 1] e Invalid (1,
        // +infinity]
        if (percentage <= 0 || percentage > 1) {
            throw new IllegalArgumentException("Percentual de perda deve estar entre 0 e 1 (exclusivo de 0).");
        }

        // A mutação que sobreviveu aqui não faz sentido
        if (this.gold <= 0.0) {
            return 0.0; // Ouro já é zero, nada a perder
        }

        double amountLost = this.gold * percentage;
        this.gold -= amountLost;

        return amountLost;
    }
}
