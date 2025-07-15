package com.simulador.criaturas.domain.model;

import java.util.ArrayList;
import java.util.List;

import com.simulador.criaturas.domain.behaviors.HorizonEntities;
import com.simulador.criaturas.utils.SimulationStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Classe que representa o horizonte, contendo uma lista de entidades do horizonte
 * (que podem ser tanto unidades de criaturas, aglomerados de criaturas e Guardiões).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Horizon {

    private List<HorizonEntities> entities = new ArrayList<>();
    private Guardian guardiao;
    private SimulationStatus status = SimulationStatus.RUNNING;

    /**
     * Inicializa as entidades do horizonte.
     *
     * @param amount a quantidade de entidades a serem criadas.
     * @return Nenhum retorno.
     * @pre Amount deve ser um número inteiro positivo maior que zero.
     * @post Entidades são criadas e adicionadas ao horizonte.
     * @throws IllegalArgumentException se amount for negativo
     */
    public void initializeEntities(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("A quantidade de criaturas deve ser positiva.");
        }
        for (int i = 1; i <= amount; i++) {
            entities.add(new CreatureUnit(i));
        }
    }

    /**
     * Adiciona uma nova entidade ao horizonte.
     *
     * @param entity a entidade a ser adicionada
     * @return Nenhum retorno.
     * @pre entity não pode ser nula.
     * @post a entidade é adicionada ao horizonte.
     * @throws IllegalArgumentException se entity for nula
     */
    public void addEntity(HorizonEntities entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Não é permitido adicionar uma entidade nula ao horizonte.");
        }
        this.entities.add(entity);
    }

    /**
     * Remove uma entidade do horizonte.
     *
     * @param entity a entidade a ser removida
     * @return Nenhum retorno.
     * @pre entity não pode ser nula
     * @post a entidade é removida do horizonte
     * @throws IllegalArgumentException se entity for nula
     */
    public void removeEntity(HorizonEntities entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Não é permitido remover uma entidade nula do horizonte.");
        }
        this.entities.remove(entity);
    }

    /**
     * Remove uma lista de entidades do horizonte.
     *
     * @param toRemove a lista de entidades a serem removidas
     * @return Nenhum retorno.
     * @pre toRemove não pode ser nula e não pode conter elementos nulos
     * @post as entidades na lista toRemove são removidas do horizonte
     * @throws IllegalArgumentException se toRemove for nula ou contiver
     * elementos nulos
     */
    public void removeEntities(List<HorizonEntities> toRemove) {
        if (toRemove == null || toRemove.stream().anyMatch(e -> e == null)) {
            throw new IllegalArgumentException("A lista de entidades a serem removidas não pode ser nula nem conter elementos nulos.");
        }
        this.entities.removeAll(toRemove);
    }

    /**
     * Obtém todas as entidades em uma determinada posição no horizonte.
     *
     * @param position a posição a ser verificada
     * @return uma lista de entidades na posição especificada
     * @pre position deve ser um número válido
     * @post a lista retornada contém todas as entidades na posição especificada
     * @throws IllegalArgumentException se position não for um número válido
     */
    public List<HorizonEntities> getEntitiesInPosition(double position) {
        if (Double.isNaN(position) || Double.isInfinite(position)) {
            throw new IllegalArgumentException("A posição não pode ser NaN ou Infinita.");
        }
        return this.entities.stream()
                .filter(e -> e.getX() == position)
                .toList();
    }

    /**
     * Retorna uma lista de todas as entidades que estão dentro de uma
     * determinada faixa de uma posição central.
     *
     * @param centerPosition A posição central da busca.
     * @param range A distância máxima do centro para ser considerado "dentro da
     * faixa".
     * @return Uma lista de entidades que atendem ao critério.
     * @pre centerPosition e range devem ser números positivos válidos.
     * @post A lista retornada contém todas as entidades cuja posição X está
     * dentro da faixa definida por centerPosition ± range.
     * @throws IllegalArgumentException se centerPosition for NaN, infinito ou
     * se range for negativo.
     */
    public List<HorizonEntities> getEntitiesWithinRange(double centerPosition, double range) {
        if (Double.isNaN(centerPosition) || Double.isInfinite(centerPosition) || centerPosition < 0) {
            throw new IllegalArgumentException("A posição central não pode ser NaN, Infinita ou negativa.");
        }

        if (Double.isNaN(range) || Double.isInfinite(range) || range < 0) {
            throw new IllegalArgumentException("A faixa deve ser um número positivo válido.");
        }

        List<HorizonEntities> foundEntities = new ArrayList<>();
        // Inclui a busca na lista principal de entidades
        for (HorizonEntities entity : this.getEntities()) {
            if (Math.abs(entity.getX() - centerPosition) <= range) {
                foundEntities.add(entity);
            }
        }

        return foundEntities;
    }
}
