package com.simulador.criaturas.domain.service;

import java.util.ArrayList;
import java.util.List;

import com.simulador.criaturas.domain.behaviors.Fusion;
import com.simulador.criaturas.domain.behaviors.HorizonEntities;
import com.simulador.criaturas.domain.behaviors.LoseGold;
import com.simulador.criaturas.domain.behaviors.Move;
import com.simulador.criaturas.domain.behaviors.StealGold;
import com.simulador.criaturas.domain.model.CreatureCluster;
import com.simulador.criaturas.domain.model.Guardian;
import com.simulador.criaturas.domain.model.Horizon;
import com.simulador.criaturas.domain.port.out.RandomPort;
import com.simulador.criaturas.utils.SimulationStatus;

public class Simulation {

    private final RandomPort randomPort;

    public Simulation(RandomPort randomPort) {
        this.randomPort = randomPort;
    }

    // Dentro da classe ServicoDeSimulacao
    /**
     * Encontra a entidade vizinha mais próxima (esquerda ou direita) de uma
     * dada entidade.
     */
    private HorizonEntities findNearestNeighbor(Horizon horizonte, HorizonEntities currentEntity) {
        List<HorizonEntities> allEntities = new ArrayList<>(horizonte.getEntities());

        // Remove a própria entidade da lista de candidatos a vizinho
        allEntities.remove(currentEntity);

        if (allEntities.isEmpty()) {
            return null;
        }

        HorizonEntities nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (HorizonEntities neighbor : allEntities) {
            double distance = Math.abs(currentEntity.getX() - neighbor.getX());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = neighbor;
            }
        }
        return nearest;
    }

    /**
     * Trata a lógica de uma entidade roubar metade do ouro do vizinho mais
     * próximo.
     */
    private void treatNeighborTheft(Horizon horizonte, HorizonEntities attacker) {
        // Apenas entidades que podem roubar ouro agem.
        if (!(attacker instanceof StealGold ladrao)) {
            return;
        }

        // Encontra o vizinho mais próximo do ladrão.
        HorizonEntities victim = findNearestNeighbor(horizonte, attacker);

        if (victim != null && victim instanceof LoseGold victimGold) {
            // A vítima deve ser capaz de perder ouro, ou seja, ter o comportamento LoseGold.
            // E o ladrão deve ser capaz de roubar ouro, ou seja, ter o comportamento StealGold.

            // Vitima perde metade do ouro.
            double stolenAmount = victimGold.loseGold(0.5);

            // Ladrão rouba metade do ouro da vítima.
            ladrao.stealGold(stolenAmount);
        }
    }

    // Dentro da classe ServicoDeSimulacao
    /**
     * Resolve TODAS as interações em uma dada posição, aplicando a hierarquia
     * de regras. Substitui os métodos antigos de colisão.
     *
     * @return A entidade "sobrevivente" que agora ocupa a posição.
     */
    private HorizonEntities resolveInteractionsAt(Horizon horizonte, double position) {
        if (horizonte == null || Double.isNaN(position) || Double.isInfinite(position)) {
            throw new IllegalArgumentException("Horizon não pode ser nulo e a posição deve ser um número válido.");
        }

        List<HorizonEntities> entitiesAtPosition = horizonte.getEntitiesInPosition(position);

        // --- REGRA DE PRIORIDADE 1: Houve colisão?
        if (entitiesAtPosition.size() <= 1) {
            // Se não houver colisão, não há nada a fazer.
            return entitiesAtPosition.isEmpty() ? null : entitiesAtPosition.get(0);
        }

        Guardian guardiao = horizonte.getGuardiao();
        boolean guardianIsAtPosition = (guardiao != null && guardiao.getX() == position);

        // --- REGRA DE PRIORIDADE 2: INTERAÇÃO COM O GUARDIÃO ---
        if (guardianIsAtPosition) {
            // Encontra qualquer cluster na mesma posição do guardião
            CreatureCluster clusterVictim = entitiesAtPosition.stream()
                    .filter(e -> e instanceof CreatureCluster)
                    .map(e -> (CreatureCluster) e)
                    .findFirst()
                    .orElse(null);

            if (clusterVictim != null && guardiao instanceof StealGold) {
                // Guardião elimina o cluster e absorve o ouro
                ((StealGold) guardiao).stealGold(clusterVictim.getGold());
                horizonte.removeEntity(clusterVictim);
                // O sobrevivente aqui é o Guardião
                return guardiao;
            }
        }

        // --- REGRA DE PRIORIDADE 3: FUSÃO DE CLUSTERS OU CRIATURAS ---
        Fusion baseFusion = entitiesAtPosition.stream()
                .filter(c -> c instanceof Fusion)
                .map(c -> (Fusion) c)
                .findFirst().orElse(null);

        if (baseFusion != null) { // Caso 1: Entre as entidades há um cluster
            List<HorizonEntities> toBeAbsorbed = entitiesAtPosition.stream()
                    .filter(c -> c != baseFusion).toList();
            for (HorizonEntities victim : toBeAbsorbed) {
                baseFusion.fusion(victim);
            }
            horizonte.removeEntities(toBeAbsorbed);
            return (HorizonEntities) baseFusion;

        } else { // Caso 2: Entre as entidades há apenas criaturas individuais
            HorizonEntities baseCreature = entitiesAtPosition.get(0);
            CreatureCluster novoCluster = new CreatureCluster(baseCreature.getId(), baseCreature.getX(), baseCreature.getGold());
            List<HorizonEntities> toBeAbsorbed = entitiesAtPosition.subList(1, entitiesAtPosition.size());

            for (HorizonEntities victim : toBeAbsorbed) {
                novoCluster.fusion(victim);
            }
            horizonte.removeEntities(entitiesAtPosition);
            horizonte.addEntity(novoCluster);
            return novoCluster;
        }
    }

    /**
     * Orquestra uma única iteração completa da simulação. VERSÃO FINAL COM
     * LÓGICA DE INTERAÇÃO UNIFICADA.
     */
    public Horizon runIteration(Horizon horizonte) {
        // 1. Criar uma cópia da lista para um roteiro de iteração seguro
        List<HorizonEntities> toProcess = new ArrayList<>(horizonte.getEntities());

        for (HorizonEntities entity : toProcess) {
            if (entity != null) {
                // 2. Verificar se a entidade ainda existe na simulação "ao vivo"
                if (!horizonte.getEntities().contains(entity)) {
                    continue;
                }

                // 3. Movimento
                if (entity instanceof Move movel) {
                    movel.move(randomPort.nextFactor());
                }

                // 4. Interação: Resolve colisões e retorna o sobrevivente
                HorizonEntities survivor = resolveInteractionsAt(horizonte, entity.getX());

                // 5. Roubo: O sobrevivente age
                if (survivor != null && !(survivor instanceof Guardian)) {
                    treatNeighborTheft(horizonte, survivor);
                }
            }
        }

        // --- Processa o Guardião ---
        Guardian guardiao = horizonte.getGuardiao();
        if (guardiao != null) {
            if (guardiao instanceof Move) {
                guardiao.move(randomPort.nextFactor());
            }
            resolveInteractionsAt(horizonte, guardiao.getX());
        }

        // 6. Ao final de todas as ações, verifica qual é o novo status do jogo
        SimulationStatus novoStatus = getStatus(horizonte);

        // 7. Atualiza o objeto horizonte com este novo status
        horizonte.setStatus(novoStatus);

        // 8. Retorna o horizonte com seu estado final atualizado
        return horizonte;
    }

    /**
     * Verifica o estado atual da simulação (Em Andamento, Sucesso ou Falha).
     *
     * @param horizonte O estado atual do jogo.
     * @return O status correspondente da simulação.
     */
    public SimulationStatus getStatus(Horizon horizonte) {
        List<HorizonEntities> remainingEntities = horizonte.getEntities();
        Guardian guardiao = horizonte.getGuardiao();

        // --- CONDIÇÕES DE SUCESSO ---
        if (remainingEntities.isEmpty()) {
            return SimulationStatus.SUCCESSFUL; // Apenas o guardião restou.
        }
        if (remainingEntities.size() == 1) {
            if (guardiao.getGold() > remainingEntities.get(0).getGold()) {
                return SimulationStatus.SUCCESSFUL; // Guardião tem mais ouro.
            }
        }

        // --- CONDIÇÃO DE FALHA (STALEMATE) ---
        if (remainingEntities.size() == 1) {
            if (guardiao.getGold() <= remainingEntities.get(0).getGold()) {
                return SimulationStatus.FAILED; // Empate/derrota, o jogo não pode mais ser ganho.
            }
        }

        // --- CONDIÇÃO PADRÃO ---
        // Se nenhuma das condições de término foi atingida, a simulação continua.
        return SimulationStatus.RUNNING;
    }

}
