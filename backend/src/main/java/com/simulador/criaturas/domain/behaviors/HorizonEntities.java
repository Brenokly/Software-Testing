package com.simulador.criaturas.domain.behaviors;

/**
 * Define o ESTADO MÍNIMO de qualquer entidade que existe na simulação. Não
 * contém comportamentos (verbos), apenas propriedades (substantivos).
 */
public interface HorizonEntities {

    int getId();

    double getX();

    void setX(double newX);

    double getGold();

    void setGold(double newGold);
}
