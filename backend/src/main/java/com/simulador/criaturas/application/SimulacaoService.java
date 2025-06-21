package com.simulador.criaturas.application;

import org.springframework.stereotype.Service;

import com.simulador.criaturas.domain.model.Horizon;
import com.simulador.criaturas.domain.port.in.SimulacaoUseCase;
import com.simulador.criaturas.domain.port.in.UserUseCase;
import com.simulador.criaturas.domain.service.Simulation;

import lombok.RequiredArgsConstructor;

/**
 * Serviço de Aplicação que implementa os casos de uso da simulação. Ele
 * orquestra o serviço de domínio para executar as ações.
 */
@RequiredArgsConstructor
@Service
public class SimulacaoService implements SimulacaoUseCase {

    // O Serviço de Aplicação DEPENDE do Serviço de Domínio
    private final Simulation servicoDeDominio;
    private final UserUseCase userUseCase;

    @Override
    public Horizon initNewSimulation(int numeroDeCriaturas) {
        // A lógica de criação do Horizonte pertence ao próprio objeto de domínio
        return new Horizon(numeroDeCriaturas, numeroDeCriaturas + 1);
    }

    @Override
    public Horizon runNextSimulation(Horizon estadoAtual, Long userId) {
        // 1. Executa a lógica de domínio para avançar um turno
        Horizon novoHorizonte = servicoDeDominio.runIteration(estadoAtual);

        // 2. Verifica se a simulação TERMINOU nesta iteração
        boolean isFinished = novoHorizonte.isSimulationSuccessful(); // Ou uma lógica de "isFinished()"

        if (isFinished) {
            // Se terminou, atualiza as estatísticas do usuário
            updateUserStatsAfterSimulation(userId, novoHorizonte.isSimulationSuccessful());
        }

        return novoHorizonte;
    }

    @Override
    public Horizon runFullSimulation(int numeroDeCriaturas, Long userId) {
        Horizon horizonte = initNewSimulation(numeroDeCriaturas);

        int maxIteracoes = 10000;
        int contador = 0;

        while (!servicoDeDominio.isSimulationSuccessful(horizonte) && contador < maxIteracoes) {
            servicoDeDominio.runIteration(horizonte);
            contador++;
        }

        // Atualiza o estado final do objeto horizonte
        horizonte.setSimulationSuccessful(servicoDeDominio.isSimulationSuccessful(horizonte));

        // Ao final da simulação completa, atualiza as estatísticas do usuário
        updateUserStatsAfterSimulation(userId, horizonte.isSimulationSuccessful());

        return horizonte;
    }

    /**
     * Método auxiliar privado para centralizar a lógica de atualização de
     * estatísticas.
     */
    private void updateUserStatsAfterSimulation(Long userId, boolean wasSuccessful) {
        // Primeiro, sempre incrementamos o número de simulações executadas.
        userUseCase.incrementSimulationsRun(userId);

        // Se a simulação foi bem-sucedida, também incrementamos a pontuação.
        if (wasSuccessful) {
            userUseCase.incrementScore(userId);
        }
    }
}
