package com.simulador.criaturas.domain.behaviors;

public interface Move {
    // Interface que define o comportamento de movimento!

    double getX();

    double getGold();

    void setX(double newX);

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
    default public void move(double randomR) {
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
}
