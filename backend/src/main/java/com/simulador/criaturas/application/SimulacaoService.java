package com.simulador.criaturas.application;

import org.springframework.stereotype.Service;

import com.simulador.criaturas.domain.model.Horizon;
import com.simulador.criaturas.domain.port.in.SimulacaoUseCase;
import com.simulador.criaturas.domain.port.in.UserUseCase;
import com.simulador.criaturas.domain.service.Simulation;
import com.simulador.criaturas.utils.SimulationStatus;

import lombok.RequiredArgsConstructor;

/**
 * {@inheritDoc} Esta classe é a implementação principal da porta
 * SimulacaoUseCase, atuando como um serviço de aplicação que orquestra a lógica
 * de domínio e as interações com outros casos de uso (como o de usuário).
 */
@Service
@RequiredArgsConstructor
public class SimulacaoService implements SimulacaoUseCase {

    private final Simulation servicoDeDominio;
    private final UserUseCase userUseCase;

    /**
     * {@inheritDoc}
     * <p>
     * A criação do Horizonte é delegada ao serviço de domínio, que é
     * responsável por validar as regras de negócio (ex: intervalo do número de
     * criaturas).
     */
    @Override
    public Horizon initNewSimulation(int numeroDeCriaturas) {
        // A validação da regra de negócio (se numeroDeCriaturas está em [1, 10])
        // é responsabilidade do serviço de domínio.
        // Assumimos que createNewSimulation agora lança IllegalArgumentException
        // se a regra for violada, cumprindo o contrato da interface.
        return servicoDeDominio.createNewSimulation(numeroDeCriaturas);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Antes de delegar, este método valida os parâmetros para evitar
     * IllegalArgumentException e cumpre o contrato de exceções da interface.
     */
    @Override
    public Horizon runNextSimulation(Horizon estadoAtual, Long userId) {
        // Validação de guarda (Fail-Fast) para cumprir o contrato.
        if (userId == null) {
            throw new IllegalArgumentException("O ID do usuário não pode ser nulo.");
        }

        // A validação de estado (se a simulação já terminou) é responsabilidade
        // do serviço de domínio, que deve lançar IllegalStateException.
        Horizon novoHorizonte = servicoDeDominio.runIteration(estadoAtual);

        if (novoHorizonte.getStatus() != SimulationStatus.RUNNING) {
            updateUserStatsAfterSimulation(userId, novoHorizonte.getStatus() == SimulationStatus.SUCCESSFUL);
        }

        return novoHorizonte;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Valida os parâmetros de entrada antes de iniciar o fluxo de simulação.
     */
    @Override
    public Horizon runFullSimulation(int numeroDeCriaturas, Long userId) {
        // Validação de guarda para cumprir o contrato.
        if (userId == null) {
            throw new IllegalArgumentException("O ID do usuário não pode ser nulo.");
        }

        // A validação do numeroDeCriaturas é feita dentro de initNewSimulation,
        // que por sua vez delega para o serviço de domínio.
        Horizon horizonte = this.initNewSimulation(numeroDeCriaturas);

        int maxIteracoes = 10000;
        int contador = 0;

        while (horizonte.getStatus() == SimulationStatus.RUNNING && contador < maxIteracoes) {
            servicoDeDominio.runIteration(horizonte);
            contador++;
        }

        if (horizonte.getStatus() != SimulationStatus.RUNNING) {
            updateUserStatsAfterSimulation(userId, horizonte.getStatus() == SimulationStatus.SUCCESSFUL);
        }

        return horizonte;
    }

    /**
     * Método auxiliar privado para centralizar a lógica de atualização de
     * estatísticas do usuário após o término de uma simulação.
     *
     * @param userId O ID do usuário a ser atualizado.
     * @param wasSuccessful 'true' se a simulação terminou com sucesso, 'false'
     * caso contrário.
     */
    private void updateUserStatsAfterSimulation(Long userId, boolean wasSuccessful) {
        // Assumimos que os métodos de userUseCase também validam o userId e
        // lançam exceção se o usuário não for encontrado.
        userUseCase.incrementSimulationsRun(userId);
        if (wasSuccessful) {
            userUseCase.incrementScore(userId);
        }
    }
}
