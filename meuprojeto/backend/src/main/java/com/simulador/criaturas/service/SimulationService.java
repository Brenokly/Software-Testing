package com.simulador.criaturas.service;

import static com.simulador.criaturas.dtos.CreatureResponseDTO.toDTOList;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.simulador.criaturas.dtos.IterationStatusDTO;
import com.simulador.criaturas.model.Creature;
import com.simulador.criaturas.model.Creatures;

/*
 * Essa classe representa o serviço de simulação.
 * Ela é responsável por gerenciar a simulação, como criar as criaturas, iterar sobre elas e realizar ações.
 * A iteração consiste em: mover uma criatura, encontrar vizinho, roubar moedas.
 */
@Service
public class SimulationService {

    private Creatures activeCreatures; // Criaturas ativas
    private Creatures inactiveCreatures; // Criaturas inativas (Creaturas que chegaram a 0 de ouro)
    private int iterationCount = 1; // Contador de iterações
    private boolean isFinished; // Indica se a simulação terminou
    private final Random random = new Random(); // Gera números aleatórios

    public IterationStatusDTO startSimulation(int amountOfCreatures) {
        // Limite de criaturas ativas deve ser entre 2 e 10
        isFinished = (amountOfCreatures <= 1 || amountOfCreatures > 10);

        // Inicializa as criaturas com o valor recebido do front-end.
        this.activeCreatures = new Creatures(amountOfCreatures);
        this.inactiveCreatures = new Creatures(0);

        return getIterationStatus();
    }

    public IterationStatusDTO iterate() {
        // Nesse método é feita a iteração sobre a criatura corrente.
        // A iteração consiste em: mover uma criatura, encontrar vizinho, roubar moedas.
        // Se não houver criaturas suficientes, lança uma exceção.
        // A cada nova iteração, é retornado o estado atual das criaturas.
        if (isFinished) {
            return getIterationStatus();
        }

        if (validateMMCreatures()) {
            return getIterationStatus();
        }

        Creature current = activeCreatures.getCurrent();
        if (validateCreature(current)) {
            return getIterationStatus();
        }

        double r = generateRandomFactor();
        current.moveCreature(r);

        Creature neighbor = findClosestNeighbor(current);
        if (validateCreature(neighbor)) {
            return getIterationStatus();
        }

        handleGoldTransfer(current, neighbor);

        iterationCount++;
        activeCreatures.next();

        return getIterationStatus();
    }

    public IterationStatusDTO getIterationStatus() {
        // Verifica se a simulação terminou
        if (activeCreatures.getAmoutOfCreatures() <= 1 || inactiveCreatures.getAmoutOfCreatures() > 10) {
            isFinished = true;
        }

        // Retorna o estado atual da simulação, incluindo as criaturas ativas e
        // inativas.
        return IterationStatusDTO.toDTO(
                toDTOList(activeCreatures.getCreatures()),
                toDTOList(inactiveCreatures.getCreatures()),
                iterationCount,
                isFinished);
    }

    private boolean validateMMCreatures() {
        if (activeCreatures.getAmoutOfCreatures() <= 1 || inactiveCreatures.getAmoutOfCreatures() > 10) {
            isFinished = true;
        }
        return isFinished;
    }

    private boolean validateCreature(Creature creature) {
        if (creature == null) {
            isFinished = true;
        }
        return isFinished;
    }

    private double generateRandomFactor() {
        return random.nextDouble() * 2 - 1;
    }

    private void handleGoldTransfer(Creature current, Creature neighbor) {
        double stolen = neighbor.giveMoney(0.5);
        if (stolen < 0) {
            inactiveCreatures.addCreature(activeCreatures.removeCreature(neighbor.getId())); // Remove da lista de
                                                                                             // ativas e adiciona na
                                                                                             // inativa
            validateMMCreatures(); // Pode lançar exceção se só sobrar uma
        } else {
            current.setGold(current.getGold() + stolen);
        }
    }

    private Creature findClosestNeighbor(Creature current) {
        double currentX = current.getX();

        List<Creature> others = activeCreatures.getCreatures().stream() // Filtra as criaturas que não são a corrente
                .filter(c -> c.getId() != current.getId())
                .toList();

        double closestDistance = Double.MAX_VALUE;
        Creature closestCreature = null;

        for (Creature creature : others) {
            double distance = Math.abs(currentX - creature.getX()); // Distância entre a criatura corrente e a vizinha
            if (distance < closestDistance) {
                closestDistance = distance;
                closestCreature = creature;
            }
        }

        return closestCreature;
    }

    public int creatureCurrent() {
        return activeCreatures.getCurrent().getId();
    }

    public IterationStatusDTO resetSimulation() {
        this.activeCreatures = new Creatures(activeCreatures.getAmoutOfCreatures());
        this.inactiveCreatures = new Creatures(0);
        this.isFinished = false;
        iterationCount = 0;

        return getIterationStatus();
    }

    // Método que finaliza a simulação
    public IterationStatusDTO finishSimulation() {
        this.isFinished = true;
        return getIterationStatus();
    }
}
