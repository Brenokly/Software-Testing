package com.simulador.criaturas.domain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    /**
     * Constrói uma nova instância do serviço de simulação.
     *
     * @param randomPort A porta para obter fatores de aleatoriedade, injetada
     * para garantir a testabilidade da lógica de negócio.
     * @throws IllegalArgumentException Se a porta de aleatoriedade for nula.
     * @pre A porta 'randomPort' não pode ser nula.
     * @post Uma nova instância de Simulation é criada com a dependência
     * fornecida.
     */
    public Simulation(RandomPort randomPort) {
        this.randomPort = Objects.requireNonNull(randomPort, "A porta de aleatoriedade não pode ser nula.");
    }

    /**
     * Cria um novo estado de simulação (Horizon) com um número específico de
     * criaturas.
     *
     * @param numeroDeCriaturas O número de criaturas para iniciar a simulação.
     * @return Um objeto Horizon inicializado e pronto para a simulação.
     * @throws IllegalArgumentException Se o número de criaturas estiver fora do
     * intervalo permitido [1, 10].
     * @pre O número de criaturas deve ser um valor entre 1 e 10, inclusive.
     * @post Um novo Horizon é criado com o número especificado de criaturas e
     * um guardião, com o status inicial 'RUNNING'.
     */
    public Horizon createNewSimulation(int numeroDeCriaturas) {
        if (numeroDeCriaturas <= 0 || numeroDeCriaturas > 10) {
            throw new IllegalArgumentException("O número de criaturas deve estar entre 1 e 10.");
        }

        int idGuardiao = numeroDeCriaturas + 1;

        Horizon horizon = new Horizon();
        horizon.initializeEntities(numeroDeCriaturas);
        horizon.setGuardiao(new Guardian(idGuardiao));

        return horizon;
    }

    public Horizon runIteration(Horizon horizonte) {
        if (horizonte == null) {
            throw new IllegalArgumentException("Horizon não pode ser nulo.");
        }
        if (horizonte.getStatus() != SimulationStatus.RUNNING) {
            throw new IllegalStateException("A simulação não pode ser executada pois seu status é: " + horizonte.getStatus());
        }

        List<HorizonEntities> toProcess = new ArrayList<>(horizonte.getEntities());
        for (HorizonEntities entity : toProcess) {
            if (entity != null) {
                if (!horizonte.getEntities().contains(entity)) {
                    continue;
                }
                if (entity instanceof Move movel) {
                    movel.move(randomPort.nextFactor());

                    // ===================================================================
                    // Arredondo a posição para o inteiro mais próximo para facilitar colisões.
                    // ===================================================================
                    long roundedX = Math.round(movel.getX());
                    movel.setX(roundedX);
                }

                HorizonEntities survivor = resolveInteractionsAt(horizonte, entity.getX());

                if (survivor != null && !(survivor instanceof Guardian)) {
                    treatNeighborTheft(horizonte, survivor);
                }
            }
        }

        Guardian guardiao = horizonte.getGuardiao();
        if (guardiao != null) {
            if (guardiao instanceof Move) {
                guardiao.move(randomPort.nextFactor());

                // CORREÇÃO 1 (APLICADA AO GUARDIÃO TAMBÉM)
                long roundedX = Math.round(guardiao.getX());
                guardiao.setX(roundedX);
            }
            resolveInteractionsAt(horizonte, guardiao.getX());
        }

        // ===================================================================
        // Removo todas as entidades (que não são o Guardião) com ouro <= 0.
        // ===================================================================
        horizonte.setEntities(horizonte.getEntities().stream()
                .filter(e -> e.getGold() >= 1.0)
                .toList());

        SimulationStatus novoStatus = getStatus(horizonte);
        horizonte.setStatus(novoStatus);
        return horizonte;
    }

    /**
     * Verifica o estado atual da simulação (Em Andamento, Sucesso ou Falha).
     *
     * @param horizonte O estado atual do jogo a ser avaliado.
     * @return O status correspondente da simulação.
     * @throws IllegalArgumentException Se o horizonte for nulo.
     * @pre O horizonte não pode ser nulo.
     * @post Nenhuma modificação é feita no horizonte. Um status é retornado com
     * base nas suas propriedades.
     */
    // Versão Segura
    public SimulationStatus getStatus(Horizon horizonte) {
        if (horizonte == null) {
            throw new IllegalArgumentException("Horizon não pode ser nulo.");
        }

        List<HorizonEntities> remainingEntities = horizonte.getEntities();
        Guardian guardiao = horizonte.getGuardiao();

        // 1. Primeiro, tratamos o caso da lista vazia
        if (remainingEntities.isEmpty()) {
            return SimulationStatus.SUCCESSFUL;
        }

        // Se o código chegou até aqui, temos 100% de certeza de que a lista NÃO está vazia.
        // 2. Agora, podemos tratar com segurança o caso de ter 1 elemento.
        if (remainingEntities.size() == 1) {
            // A chamada a .get(0) agora é 100% segura.
            if (guardiao.getGold() > remainingEntities.get(0).getGold()) {
                return SimulationStatus.SUCCESSFUL;
            } else {
                return SimulationStatus.FAILED;
            }
        }

        return SimulationStatus.RUNNING;
    }

    // --- MÉTODOS PRIVADOS (AUXILIARES) ---
    /**
     * Encontra a entidade vizinha mais próxima de uma entidade de referência.
     *
     * @param horizonte O contexto da simulação contendo todas as entidades.
     * @param currentEntity A entidade a partir da qual a busca é feita.
     * @return A entidade vizinha mais próxima, ou null se não houver outros
     * vizinhos.
     * @throws IllegalArgumentException Se os parâmetros de entrada forem nulos.
     * @pre 'horizonte' e 'currentEntity' não podem ser nulos.
     * @post O estado do horizonte não é modificado.
     */
    public HorizonEntities findNearestNeighbor(Horizon horizonte, HorizonEntities currentEntity) {
        if (horizonte == null || currentEntity == null) {
            throw new IllegalArgumentException("Horizon e entidade atual não podem ser nulos.");
        }

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
     * Gerencia a lógica de uma entidade atacante roubar ouro de seu vizinho.
     *
     * @param horizonte O contexto da simulação.
     * @param attacker A entidade que tenta realizar o roubo.
     * @return Nenhum retorno.
     * @pre O atacante deve ser uma instância de StealGold. O vizinho, se
     * existir, deve ser uma instância de LoseGold.
     * @post Se as condições forem atendidas, o ouro é transferido da vítima
     * para o atacante, modificando o estado de ambas as entidades.
     */
    public void treatNeighborTheft(Horizon horizonte, HorizonEntities attacker) {
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

    /**
     * Resolve todas as interações (colisões, fusões) que ocorrem em uma
     * posição.
     *
     * @param horizonte O contexto da simulação.
     * @param position A coordenada no eixo X onde as interações devem ser
     * resolvidas.
     * @return A entidade sobrevivente que permanece na posição após as
     * interações.
     * @throws IllegalArgumentException Se horizonte for nulo ou a posição for
     * inválida.
     * @pre 'horizonte' não pode ser nulo e 'position' deve ser um número
     * finito.
     * @post Entidades na posição são fundidas ou removidas. A entidade
     * sobrevivente (se houver) é retornada.
     */
    public HorizonEntities resolveInteractionsAt(Horizon horizonte, double position) {
        if (horizonte == null || Double.isNaN(position) || Double.isInfinite(position)) {
            throw new IllegalArgumentException("Horizon não pode ser nulo e a posição deve ser um número válido.");
        }

        List<HorizonEntities> entitiesAtPosition = horizonte.getEntitiesInPosition(position);

        List<CreatureCluster> creatureClusters = entitiesAtPosition.stream()
                .filter(e -> e instanceof CreatureCluster)
                .map(e -> (CreatureCluster) e)
                .toList();

        Guardian guardiao = horizonte.getGuardiao();
        boolean guardianIsAtPosition = (guardiao != null && guardiao.getX() == position);

        // --- REGRA DE PRIORIDADE 1: INTERAÇÃO COM O GUARDIÃO ---
        if (guardianIsAtPosition && !creatureClusters.isEmpty()) {
            // Encontra qualquer cluster na mesma posição do guardião
            CreatureCluster clusterVictim = creatureClusters.get(0);

            ((StealGold) guardiao).stealGold(clusterVictim.getGold());
            horizonte.removeEntity(clusterVictim);
            return guardiao;
        }

        // --- REGRA DE PRIORIDADE 2: Houve colisão?
        if (entitiesAtPosition.size() <= 1) {
            // Se não houver colisão, não há nada a fazer.
            return entitiesAtPosition.isEmpty() ? null : entitiesAtPosition.get(0);
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
}
