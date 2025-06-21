package com.simulador.criaturas.domain.port.in;

import com.simulador.criaturas.domain.model.Horizon;

/**
 * Porta de Entrada que define os Casos de Uso para manipular a simulação. Esta
 * é a API da nossa camada de aplicação.
 */
public interface SimulacaoUseCase {

    /**
     * Inicia uma nova simulação com um número específico de criaturas.
     *
     * @param numeroDeCriaturas O número de criaturas iniciais.
     * @return O estado inicial do Horizonte, pronto para a primeira iteração.
     * @pre numeroDeCriaturas > 0 && numeroDeCriaturas <= 10
     * @post O estado do Horizonte é inicializado com o número de criaturas
     * especificado.
     * @throws IllegalArgumentException Se o número de criaturas não estiver no
     * intervalo válido.
     */
    Horizon initNewSimulation(int numeroDeCriaturas);

    /**
     * Recebe o estado atual de um Horizonte e o ID do usuário, e executa uma
     * única iteração (um turno).
     *
     * @param estadoAtual O objeto Horizon atual.
     * @param userId O ID do usuário que está executando a simulação.
     * @return O objeto Horizon com o estado atualizado.
     */
    Horizon runNextSimulation(Horizon estadoAtual, Long userId);

    /**
     * Cria e executa uma simulação completa para um usuário específico.
     *
     * @param numeroDeCriaturas O número de criaturas iniciais.
     * @param userId O ID do usuário que está executando a simulação.
     * @return O estado final do Horizonte.
     */
    Horizon runFullSimulation(int numeroDeCriaturas, Long userId);
}
