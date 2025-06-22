package com.simulador.criaturas.domain.behaviors;

public interface Fusion {
    // Interface que define o comportamento de fusão de criaturas.

    public void setGold(double gold);

    public double getGold();

    /**
     * Adiciona uma criatura ao cluster, somando o ouro da criatura ao ouro do
     * cluster.
     *
     * @param creature A criatura a ser adicionada ao cluster.
     * @return Nenhum retorno.
     * @throws IllegalArgumentException Se a criatura for nula ou se a
     * quantidade de ouro da criatura não for um número finito.
     * @pre A criatura não pode ser nula e a quantidade de ouro deve ser um
     * número finito.
     * @post O ouro do cluster é atualizado com a soma do ouro da criatura
     * adicionada.
     */
    default public void fusion(HorizonEntities creature) {
        if (creature == null) {
            throw new IllegalArgumentException("Criatura não pode ser nula.");
        }

        if (!Double.isFinite(creature.getGold())) {
            throw new IllegalArgumentException("A quantidade de ouro da criatura deve ser um número finito.");
        }

        // O cluster ganha o ouro da criatura que está sendo adicionada
        this.setGold(this.getGold() + creature.getGold());
    }
}
