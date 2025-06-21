package com.simulador.criaturas.application;

import org.springframework.stereotype.Service;

import com.simulador.criaturas.domain.model.Horizon;
import com.simulador.criaturas.domain.port.in.SimulacaoUseCase;
import com.simulador.criaturas.domain.port.in.UserUseCase;
import com.simulador.criaturas.domain.service.Simulation;
import com.simulador.criaturas.utils.SimulationStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SimulacaoService implements SimulacaoUseCase {

    private final Simulation servicoDeDominio;
    private final UserUseCase userUseCase;

    @Override
    public Horizon initNewSimulation(int numeroDeCriaturas) {
        return new Horizon(numeroDeCriaturas, numeroDeCriaturas + 1);
    }

    @Override
    public Horizon runNextSimulation(Horizon estadoAtual, Long userId) {
        // 1. Executa a lógica de domínio, que retorna o horizonte com o status atualizado.
        Horizon novoHorizonte = servicoDeDominio.runIteration(estadoAtual);

        // 2. Verifica se a simulação TERMINOU (não está mais em RUNNING).
        if (novoHorizonte.getStatus() != SimulationStatus.RUNNING) {
            // 3. Se terminou, atualiza as estatísticas do usuário.
            //    Passamos 'true' para wasSuccessful apenas se o status final for SUCCESSFUL.
            updateUserStatsAfterSimulation(userId, novoHorizonte.getStatus() == SimulationStatus.SUCCESSFUL);
        }

        return novoHorizonte;
    }

    @Override
    public Horizon runFullSimulation(int numeroDeCriaturas, Long userId) {
        Horizon horizonte = initNewSimulation(numeroDeCriaturas);

        int maxIteracoes = 10000;
        int contador = 0;

        // O loop agora continua ENQUANTO o status for RUNNING.
        while (horizonte.getStatus() == SimulationStatus.RUNNING && contador < maxIteracoes) {
            // O próprio runIteration irá atualizar o status dentro do objeto 'horizonte'.
            servicoDeDominio.runIteration(horizonte);
            contador++;
        }

        // Ao final do loop, o 'horizonte' já contém o status final (SUCCESSFUL, FAILED, ou RUNNING se atingiu maxIteracoes).
        // A lógica de atualização de estatísticas é a mesma.
        // Incrementa 'simulationsRun' se o jogo terminou por qualquer motivo.
        if (horizonte.getStatus() != SimulationStatus.RUNNING) {
            userUseCase.incrementSimulationsRun(userId);
        }

        // Incrementa 'pontuation' apenas em caso de sucesso.
        if (horizonte.getStatus() == SimulationStatus.SUCCESSFUL) {
            userUseCase.incrementScore(userId);
        }

        return horizonte;
    }

    /**
     * Método auxiliar privado para centralizar a lógica de atualização de
     * estatísticas. Este método não precisa de alterações.
     */
    private void updateUserStatsAfterSimulation(Long userId, boolean wasSuccessful) {
        userUseCase.incrementSimulationsRun(userId);
        if (wasSuccessful) {
            userUseCase.incrementScore(userId);
        }
    }
}
