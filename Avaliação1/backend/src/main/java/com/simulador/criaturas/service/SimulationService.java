package com.simulador.criaturas.service;

import static com.simulador.criaturas.dtos.CreatureResponseDTO.toDTOList;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.simulador.criaturas.dtos.IterationStatusDTO;
import com.simulador.criaturas.exception.InsufficientCreatures;
import com.simulador.criaturas.model.Creature;
import com.simulador.criaturas.model.Creatures;

@Service
public class SimulationService {

    private final Random random;

    private Creatures activeCreatures;
    private Creatures inactiveCreatures;
    private int iterationCount;
    private boolean isFinished;
    private boolean isValid = false;
    private int initialAmountOfCreatures;

    public SimulationService() {
        this(new Random());
    }

    // Permite injetar um Random customizado para testes
    public SimulationService(Random random) {
        this.random = random;
    }

    /**
     * Inicia a simulação com a quantidade especificada de criaturas.
     *
     * @param amountOfCreatures a quantidade de criaturas a serem simuladas
     * @return o status da iteração após o início da simulação
     * @pre amountOfCreatures deve estar entre 2 e 10
     * @post a simulação é iniciada com a quantidade especificada de criaturas
     * @throws InsufficientCreatures se a qnt de criaturas não estiver no intervalo
     */
    public IterationStatusDTO startSimulation(int amountOfCreatures) {
        if (amountOfCreatures < 2 || amountOfCreatures > 10) {
            throw new InsufficientCreatures("A quantidade de criaturas deve estar entre 2 e 10.");
        }

        this.initialAmountOfCreatures = amountOfCreatures;
        this.isFinished = false;
        this.iterationCount = 1;

        this.activeCreatures = new Creatures(amountOfCreatures);
        this.inactiveCreatures = new Creatures(0);
        this.isValid = true;

        return getIterationStatus();
    }

    /**
     * Executa uma iteração da simulação. (Move a criatura atual e tenta roubar ouro
     * de um vizinho)
     *
     * método sem parâmetros.
     *
     * @return o status da iteração após a execução
     * @pre a simulação deve estar em um estado válido
     * @post a iteração é executada e o status é atualizado
     * @throws IllegalStateException se a simulação não foi iniciada corretamente ou
     *                               IllegalArgumentException caso alguma criatura
     *                               esteja com problema
     */
    public IterationStatusDTO iterate() {
        if (!isValid) {
            throw new IllegalStateException("A simulação não foi iniciada corretamente.");
        }

        if (isFinished || shouldFinishDueToCreatureCount()) {
            isFinished = true;
            return getIterationStatus();
        }

        Creature current = activeCreatures.getCurrent();
        if (current == null) {
            isFinished = true;
            return getIterationStatus();
        }

        current.moveCreature(generateRandomFactor());

        Creature neighbor = findClosestNeighbor(current);
        if (neighbor == null) {
            isFinished = true;
            return getIterationStatus();
        }

        current.stealGoldFrom(neighbor, 0.5);

        if (neighbor.getGold() < 1) {
            inactiveCreatures.addCreature(activeCreatures.removeCreature(neighbor.getId()));
        }

        if (shouldFinishDueToCreatureCount()) {
            isFinished = true;
        }

        iterationCount++;
        if (activeCreatures.getAmountOfCreatures() > 1) {
            activeCreatures.next();
        }

        return getIterationStatus();
    }

    /**
     * Retorna o status atual da iteração.
     *
     * método sem parâmetros.
     *
     * @return o status atual da iteração
     * @pre A simulação deve estar em um estado válido
     * @post o status da iteração é retornado
     * @throws IllegalStateException se a simulação não foi iniciada corretamente
     */
    public IterationStatusDTO getIterationStatus() {
        if (!isValid) {
            throw new IllegalStateException("A simulação não foi iniciada corretamente.");
        }

        return IterationStatusDTO.toDTO(
                toDTOList(activeCreatures.getCreatures()),
                toDTOList(inactiveCreatures.getCreatures()),
                iterationCount,
                isFinished);
    }

    /**
     * Retorna a criatura atual (sem avançar).
     *
     * método sem parâmetros.
     *
     * @return A criatura atual, ou null se não houver criaturas.
     * @pre A simulação deve estar em um estado válido.
     * @post Retorna a criatura atual, ou -1 se não houver criaturas.
     * @throws IllegalStateException se a simulação não foi iniciada corretamente.
     */
    public int getCurrentCreatureId() {
        if (!isValid) {
            throw new IllegalStateException("A simulação não foi iniciada corretamente.");
        }
        Creature current = activeCreatures.getCurrent();
        return current != null ? current.getId() : -1;
    }

    /**
     * Reinicia a simulação com a quantidade inicial de criaturas.
     *
     * método sem parâmetros.
     *
     * @return o status da iteração após a reinicialização.
     * @pre startSimulation() deve ter sido chamado.
     * @post a simulação é reiniciada com a quantidade inicial de criaturas.
     * @throws Nenhuma exceção é lançada pelo método.
     */
    public IterationStatusDTO resetSimulation() {
        this.activeCreatures = new Creatures(initialAmountOfCreatures);
        this.inactiveCreatures = new Creatures(0);
        this.iterationCount = 1;
        this.isFinished = false;

        return getIterationStatus();
    }

    /**
     * Finaliza a simulação e retorna o status atual.
     *
     * método sem parâmetros.
     *
     * @return o status atual da iteração.
     * @pre nenhuma pré-condição específica.
     * @post a simulação é finalizada e o status é retornado.
     * @throws IllegalStateException se a simulação não foi iniciada corretamente.
     */
    public IterationStatusDTO finishSimulation() {
        this.isFinished = true;
        return getIterationStatus();
    }

    // === Métodos auxiliares privados ===

    /**
     * Verifica se a simulação deve ser finalizada com base na contagem de criaturas
     *
     * método sem parâmetros.
     *
     * @return true se a simulação deve ser finalizada, false caso contrário.
     * @pre nenhuma pré-condição específica.
     * @post Se retornar true, a simulação será finalizada.
     * @throws Nenhuma exceção é lançada pelo método.
     */
    private boolean shouldFinishDueToCreatureCount() {
        if (!isValid) {
            return false;
        }
        return activeCreatures.getAmountOfCreatures() < 2 || activeCreatures.getAmountOfCreatures() > 10;
    }

    /**
     * Gera um fator aleatório para movimentação da criatura.
     *
     * método sem parâmetros.
     *
     * @return um valor entre -1 e 1 representando o fator aleatório.
     * @pre nenhuma pré-condição específica.
     * @post um novo fator aleatório é gerado.
     * @throws Nenhuma exceção é lançada pelo método.
     */
    private double generateRandomFactor() {
        return random.nextDouble() * 2 - 1; // Gera valor entre -1 e 1
    }

    /**
     * Encontra a criatura vizinha mais próxima da criatura atual.
     *
     * @param current a criatura atual
     * @return a criatura vizinha mais próxima, ou null se não houver criaturas
     *         ativas
     * @pre Deve haver mais de uma criatura ativa.
     * @post Retorna a criatura vizinha mais próxima ou null.
     * @throws Nenhuma exceção é lançada pelo método.
     */
    private Creature findClosestNeighbor(Creature current) {
        if (activeCreatures.getAmountOfCreatures() <= 1) {
            isFinished = true;
            return null;
        }

        double currentX = current.getX();

        List<Creature> others = activeCreatures.getCreatures().stream()
                .filter(c -> c.getId() != current.getId())
                .toList();

        double closestDistance = Double.MAX_VALUE;
        Creature closestCreature = null;

        for (Creature creature : others) {
            double distance = Math.abs(currentX - creature.getX());
            if (distance < closestDistance) {
                closestDistance = distance;
                closestCreature = creature;
            }
        }

        return closestCreature;
    }
}
