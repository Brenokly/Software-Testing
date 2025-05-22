package com.simulador.criaturas.service;

import static com.simulador.criaturas.dtos.CreatureResponseDTO.toDTOList;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.simulador.criaturas.dtos.IterationStatusDTO;
import com.simulador.criaturas.exception.InsufficientCreatures;
import com.simulador.criaturas.model.Creature;
import com.simulador.criaturas.model.Creatures;

import lombok.Data;

@Service
@Data
public class SimulationService {

    private final Random random;

    private Creatures activeCreatures;
    private Creatures inactiveCreatures;
    private int iterationCount;
    private boolean finished;
    private boolean valid = false;
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
     * @return O status da iteração após o início da simulação
     * @pre amountOfCreatures deve estar entre 2 e 10
     * @post A simulação é iniciada com a quantidade especificada de criaturas e
     * o status da iteração é retornado
     * @throws InsufficientCreatures se a quantidade de criaturas não estiver no
     * intervalo
     */
    public IterationStatusDTO startSimulation(int amountOfCreatures) {
        if (amountOfCreatures < 2 || amountOfCreatures > 10) {
            throw new InsufficientCreatures("A quantidade de criaturas deve estar entre 2 e 10.");
        }

        this.initialAmountOfCreatures = amountOfCreatures;
        this.finished = false;
        this.iterationCount = 1;

        this.activeCreatures = new Creatures(amountOfCreatures);
        this.inactiveCreatures = new Creatures(0);
        this.valid = true;

        return getIterationStatus();
    }

    /**
     * Executa uma iteração da simulação. (Move a criatura atual e tenta roubar
     * ouro de um vizinho)
     *
     * método sem parâmetros.
     *
     * @return O status da iteração após a execução
     * @pre A simulação deve estar em um estado válido e não finalizada.
     * @post A iteração é executada, atualiza o status da simulação e retorna-o.
     * @throws IllegalStateException se a simulação não foi iniciada
     * corretamente.
     * @throws IllegalArgumentException se acontecer algum problema com as
     * criaturas.
     */
    public IterationStatusDTO iterate() {
        if (finished) {
            return getIterationStatus();
        } else if (!CheckIfTheSimulationIsValid()) {
            this.valid = false;
            throw new IllegalStateException("A simulação não foi iniciada corretamente.");
        }

        Creature current = activeCreatures.getCurrent();
        if (current == null) {
            this.valid = false;
            throw new IllegalStateException("A simulação não foi iniciada corretamente.");
        }

        current.moveCreature(generateRandomFactor());

        Creature neighbor = findClosestNeighbor(current);
        if (neighbor == null) {
            this.finished = true;
            return getIterationStatus();
        }

        current.stealGoldFrom(neighbor, 0.5);

        if (neighbor.getGold() < 1) {
            inactiveCreatures.addCreature(activeCreatures.removeCreature(neighbor.getId()));
        }

        nextIfValid();

        iterationCount++;

        return getIterationStatus();
    }

    /**
     * Retorna o status atual da iteração.
     *
     * método sem parâmetros.
     *
     * @return O status atual da iteração.
     * @pre A simulação deve estar em um estado válido.
     * @post O status da iteração é retornado.
     * @throws IllegalStateException se a simulação não foi iniciada
     * corretamente.
     */
    public IterationStatusDTO getIterationStatus() {
        if (!valid) {
            throw new IllegalStateException("A simulação não foi iniciada corretamente.");
        }

        return IterationStatusDTO.toDTO(
                toDTOList(activeCreatures.getCreatures()),
                toDTOList(inactiveCreatures.getCreatures()),
                iterationCount,
                finished);
    }

    /**
     * Retorna a criatura atual (sem avançar).
     *
     * método sem parâmetros.
     *
     * @return A criatura atual, ou null se não houver criaturas.
     * @pre A simulação deve estar em um estado válido.
     * @post Retorna a criatura atual, ou -1 se não houver criaturas.
     * @throws IllegalStateException se a simulação não foi iniciada
     * corretamente.
     */
    public int getCurrentCreatureId() {
        Creature current = activeCreatures.getCurrent();
        return current != null ? current.getId() : -1;
    }

    /**
     * Reinicia a simulação com a quantidade inicial de criaturas.
     *
     * método sem parâmetros.
     *
     * @return O status da iteração após a reinicialização.
     * @pre startSimulation() deve ter sido chamado e deve ter sido executado
     *
     * sem problemas.
     * @post A simulação é reiniciada com a quantidade inicial de criaturas.
     * @throws Nenhuma exceção é lançada pelo método.
     */
    public IterationStatusDTO resetSimulation() {
        this.activeCreatures = new Creatures(initialAmountOfCreatures);
        this.inactiveCreatures = new Creatures(0);
        this.iterationCount = 1;
        this.finished = false;

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
     * @throws IllegalStateException se a simulação não foi iniciada
     * corretamente.
     */
    public IterationStatusDTO finishSimulation() {
        this.finished = true;
        return getIterationStatus();
    }

    /**
     * Verifica se a simulação está com o número de criaturas válido.
     *
     * método sem parâmetros.
     *
     * @return True se o número de criaturas estiver entre 2 e 10, false caso
     * contrário.
     * @pre nenhuma pré-condição específica.
     * @post retorna true se o número de criaturas for válido.
     * @throws Nenhuma exceção é lançada pelo método.
     */
    protected boolean CheckIfTheSimulationIsValid() {
        return activeCreatures.getAmountOfCreatures() >= 2 && activeCreatures.getAmountOfCreatures() <= 10;
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
    protected double generateRandomFactor() {
        return random.nextDouble() * 2 - 1; // Gera valor entre -1 e 1
    }

    /**
     * Encontra a criatura vizinha mais próxima da criatura atual.
     *
     * @param current A criatura atual
     * @return a criatura vizinha mais próxima, ou null se não houver criaturas
     * ativas
     * @pre Deve haver mais de uma criatura ativa.
     * @post Retorna a criatura vizinha mais próxima ou null.
     * @throws Nenhuma exceção é lançada pelo método.
     */
    protected Creature findClosestNeighbor(Creature current) {
        if (current == null) {
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

    /**
     * Avança para a próxima iteração se a simulação for válida.
     *
     * método sem parâmetros.
     *
     * @pre nenhuma pré-condição específica.
     * @post CurrentCreature é atualizado para a próxima criatura ativa.
     * @throws Nenhuma exceção é lançada pelo método.
     */
    protected void nextIfValid() {
        if (CheckIfTheSimulationIsValid()) {
            activeCreatures.next();
        } else {
            this.finished = true;
        }
    }
}