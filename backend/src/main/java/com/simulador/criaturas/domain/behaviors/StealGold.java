package com.simulador.criaturas.domain.behaviors;

public interface StealGold {
    // Interface que define o comportamento de roubo de ouro!

    double getGold();

    void setGold(double newGold);

    /**
     * Rouba ouro de outra criatura.
     *
     * @param amountStolen Quantidade de ouro a ser roubada da vítima.
     * @return Quantidade de ouro roubada da vítima.
     * @throws IllegalArgumentException se precondições ou poscondições forem
     * violadas.
     * @pre amountStolen > 0 e amountStolen é um número finito.
     * @post Ouro da cratura aumenta ou permanece o mesmo se o ouro roubado for
     * < 0 ou == 0.
     */
    default public double stealGold(double amountStolen) {
        // Evita NaN ou infinitos causados por somas com valores inválidos
        if (!Double.isFinite(amountStolen)) {
            throw new IllegalArgumentException("Roubo inválido: valor roubado não pode ser infinito.");
        } else if (amountStolen == 0 || amountStolen < 0) {
            return 0; // Nada a roubar
        }

        // Neste ponto, amountStolen é garantidamente > 0 e finito
        double amount = this.getGold() + amountStolen;
        this.setGold(amount);

        return amountStolen;
    }
}
