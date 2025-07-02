package com.simulador.criaturas.domain.behaviors;

public interface LoseGold {
    // Interface que define o comportamento de perda de ouro!

    double getGold();

    void setGold(double newGold);

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
        // Domínio: 0 < percentage <= 1
        // Partições: Invalid [-infinity, 0], Valid (0 < percentage <= 1] e Invalid (1, +infinity]
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
}
