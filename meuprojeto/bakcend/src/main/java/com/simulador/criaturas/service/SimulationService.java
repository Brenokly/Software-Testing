package com.simulador.criaturas.service;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import static com.simulador.criaturas.dtos.CreatureResponseDTO.toDTOList;
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

    public void startSimulation(int amountOfCreatures) {
        if (amountOfCreatures <= 1) {
            throw new IllegalArgumentException("Deve haver mais de uma criatura para a simulação.");
        }

        // Inicializa as criaturas com o valor recebido do front-end.
        this.activeCreatures = new Creatures(amountOfCreatures);
        this.inactiveCreatures = new Creatures(0);
        this.isFinished = false;
    }

    public IterationStatusDTO iterate() {
        // Nesse método é feita a iteração sobre a criatura corrente.
        // A iteração consiste em: mover uma criatura, encontrar vizinho, roubar moedas.
        // Se não houver criaturas suficientes, lança uma exceção.
        // A cada nova iteração, é retornado o estado atual das criaturas.

        validateMinimumCreatures();

        Creature current = activeCreatures.getCurrent();
        validateCreature(current);

        double r = generateRandomFactor();
        current.moveCreature(r);

        Creature neighbor = findClosestNeighbor(current);
        validateNeighbor(neighbor);

        handleGoldTransfer(current, neighbor);

        iterationCount++;
        activeCreatures.next();

        return getIterationStatus();
    }

    private IterationStatusDTO getIterationStatus() {
        // Verifica se a simulação terminou
        if (activeCreatures.getAmoutOfCreatures() <= 1) {
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

    private void validateMinimumCreatures() {
        if (activeCreatures.getAmoutOfCreatures() <= 1) {
            throw new IllegalStateException("Não há criaturas suficientes para iterar.");
        }
    }

    private void validateCreature(Creature creature) {
        if (creature == null) {
            throw new IllegalStateException("Houve algum erro ao pegar a criatura corrente.");
        }
    }

    private void validateNeighbor(Creature neighbor) {
        if (neighbor == null) {
            throw new IllegalStateException("Houve algum erro ao encontrar o vizinho mais próximo.");
        }
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
            validateMinimumCreatures(); // Pode lançar exceção se só sobrar uma
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

    public void resetSimulation() {
        this.activeCreatures = new Creatures(activeCreatures.getAmoutOfCreatures());
        this.inactiveCreatures = new Creatures(0);
        this.isFinished = false;
        iterationCount = 0;
    }

}
