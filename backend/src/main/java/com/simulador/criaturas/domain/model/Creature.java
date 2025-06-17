package com.simulador.criaturas.domain.model;

public interface Creature {

    //-----------------------------------------------------------------------------------------
    // MÉTODOS DE NÉGÓCIO PADRÃO PARA TODA CRIATURA
    /**
     * Move a criatura com base em um fator aleatório e na quantidade de ouro.
     *
     * @param randomR Valor aleatório no intervalo [-1, 1].
     * @return Nenhum retorno.
     * @throws IllegalArgumentException se randomR estiver fora do intervalo
     * [-1, 1] ou se randomR for NaN ou infinito.
     * @pre -1 <= randomR <= 1.
     * @post X será atualizado com X + (randomR * Gold).
     */
    default public void moveCreature(double randomR) {
        // Domínio: -1 <= randomR <= 1, Fronteira: -1 <= randomR <= 1
        // Partições: Invalid [-infinity, -1), Valid [-1, 1], Invalid (1, +infinity]
        if (Double.isNaN(randomR) || randomR < -1 || randomR > 1) {
            throw new IllegalArgumentException("Valor aleatório deve estar entre -1 e 1.");
        }

        // Lógica de negócio usando os métodos da PRÓPRIA interface
        double currentX = getX();
        double currentGold = getGold();
        double newX = currentX + (randomR * currentGold);
        setX(newX);
    }

    /**
     * Rouba ouro de outra criatura.
     *
     * @param victim Criatura alvo do roubo.
     * @param percentage Percentual do ouro a ser roubado (onde 0 < percentage
     * <= 1).
     * @return Quantidade de ouro roubada da vítima.
     * @throws IllegalArgumentException se precondições ou poscondições forem
     * violadas.
     * @pre victim != null && 0 < percentage <= 1
     * @post Ouro da vítima é reduzido em percentage e o ouro do ladrão é
     * aumentado.
     */
    default public double stealGoldFrom(Creature victim, double percentage) {
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
        double amount = this.getGold() + amountStolen;
        this.setGold(amount);

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
    default public double loseGold(double percentage) {
        // Domínio: 0 < percentage <= 1, Fronteira: 0 < percentage <= 1
        // Partições: Invalid [-infinity, 0], Valid (0 < percentage <= 1] e Invalid (1,
        // +infinity]
        if (percentage <= 0 || percentage > 1) {
            throw new IllegalArgumentException("Percentual de perda deve estar entre 0 e 1 (exclusivo de 0).");
        }

        // A mutação que sobreviveu aqui não faz sentido
        if (this.getGold() <= 0.0) {
            return 0.0; // Ouro já é zero, nada a perder
        }

        // Calcula a quantidade de ouro perdida
        double amountLost = this.getGold() * percentage;
        double rest = this.getGold() - amountLost;

        // Seta a quantidade de ouro restante
        this.setGold(rest);

        return amountLost;
    }

    //-----------------------------------------------------------------------------------------
    // MÉTODOS OBRIGATÓRIOS (CONTRATOS)
    double getX();

    void setX(double newX);

    double getGold();

    void setGold(double newGold);

    int getId();
}
