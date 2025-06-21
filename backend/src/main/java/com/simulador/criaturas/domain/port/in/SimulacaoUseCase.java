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
     * Recebe o estado atual de um Horizonte e executa uma única iteração (um
     * turno). Ideal para o modelo stateless onde o frontend envia o estado e
     * recebe o novo.
     *
     * @param estadoAtual O objeto Horizon representando o estado atual da
     * simulação.
     * @return O objeto Horizon com o estado atualizado após a iteração.
     * @pre estadoAtual != null
     * @post O estado do Horizonte é atualizado com base nas regras de
     * simulação.
     */
    Horizon runNextSimulation(Horizon estadoAtual);

    /**
     * Cria uma nova simulação e a executa completamente até o fim. Útil para
     * cenários onde o usuário quer ver o resultado final de uma vez.
     *
     * @param numeroDeCriaturas O número de criaturas iniciais.
     * @return O estado final do Horizonte após a simulação ser concluída.
     * @pre numeroDeCriaturas > 0 && numeroDeCriaturas <= 10
     * @post O estado do Horizonte é atualizado com o resultado final da
     * simulação.
     * @throws IllegalArgumentException Se o número de criaturas não estiver no
     * intervalo válido.
     */
    Horizon runFullSimulation(int numeroDeCriaturas);
}
