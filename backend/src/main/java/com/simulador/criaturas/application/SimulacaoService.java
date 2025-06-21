package com.simulador.criaturas.application;

import com.simulador.criaturas.domain.model.Horizon;
import com.simulador.criaturas.domain.port.in.SimulacaoUseCase;
import com.simulador.criaturas.domain.service.Simulation;

import lombok.RequiredArgsConstructor;

/**
 * Serviço de Aplicação que implementa os casos de uso da simulação. Ele
 * orquestra o serviço de domínio para executar as ações.
 */
@RequiredArgsConstructor
public class SimulacaoService implements SimulacaoUseCase {

    // O Serviço de Aplicação DEPENDE do Serviço de Domínio
    private final Simulation servicoDeDominio;

    @Override
    public Horizon initNewSimulation(int numeroDeCriaturas) {
        // A lógica de criação do Horizonte pertence ao próprio objeto de domínio
        return new Horizon(numeroDeCriaturas, numeroDeCriaturas + 1);
    }

    @Override
    public Horizon runNextSimulation(Horizon estadoAtual) {
        // Ele simplesmente orquestra a chamada para o domínio
        return servicoDeDominio.runIteration(estadoAtual);
    }

    @Override
    public Horizon runFullSimulation(int numeroDeCriaturas) {
        Horizon horizonte = initNewSimulation(numeroDeCriaturas);

        // Adicionamos um limite de segurança para evitar loops infinitos
        int maxIteracoes = 10000;
        int contador = 0;

        while (!horizonte.isSimulationSuccessful() && contador < maxIteracoes) {
            // Reutilizamos a lógica de executar uma iteração
            servicoDeDominio.runIteration(horizonte);
            contador++;
        }

        // Aqui, após a simulação completa, você poderia chamar outro serviço
        // para, por exemplo, salvar a pontuação do usuário.
        // if(horizonte.isSimulationSuccessful()){
        //     userService.incrementarPontuacao(userId);
        // }
        return horizonte;
    }
}
