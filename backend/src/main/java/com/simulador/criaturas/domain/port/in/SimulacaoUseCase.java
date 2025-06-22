package com.simulador.criaturas.domain.port.in;

import com.simulador.criaturas.domain.model.Horizon;

/**
 * Porta de Entrada que define os Casos de Uso para manipular a simulação.
 */
public interface SimulacaoUseCase {

    /**
     * Inicia uma nova simulação com um número específico de criaturas.
     *
     * @param numeroDeCriaturas O número de criaturas para iniciar a simulação.
     * @return O estado inicial do Horizonte, pronto para a primeira iteração.
     * @throws IllegalArgumentException Se o número de criaturas for inválido
     * (fora do intervalo permitido).
     * @pre O número de criaturas deve ser maior que zero e estar dentro do
     * limite máximo estabelecido pelas regras de negócio. [1,10]
     * @post Um novo objeto Horizon é criado e retornado, populado com o número
     * especificado de criaturas e pronto para o início da simulação.
     */
    Horizon initNewSimulation(int numeroDeCriaturas);

    /**
     * Executa um único turno (iteração) da simulação a partir de um estado
     * existente.
     *
     * @param estadoAtual O objeto Horizon representando o estado atual da
     * simulação.
     * @param userId O ID do usuário que está executando a simulação, para fins
     * de pontuação.
     * @return O objeto Horizon com o estado atualizado após a execução do
     * turno.
     * @throws IllegalArgumentException Se o 'estadoAtual' ou 'userId' forem
     * nulos.
     * @throws IllegalStateException Se a simulação já tiver terminado (ex:
     * resta apenas uma criatura).
     * @pre O 'estadoAtual' não pode ser nulo e deve representar uma simulação
     * em andamento. O 'userId' não pode ser nulo e deve corresponder a um
     * usuário válido.
     * @post O estado do Horizonte é avançado em um turno, com criaturas se
     * movendo, interagindo e o contador de turnos sendo incrementado. O objeto
     * retornado reflete esse novo estado.
     */
    Horizon runNextSimulation(Horizon estadoAtual, Long userId);

    /**
     * Cria e executa uma simulação completa, do início ao fim, para um usuário.
     *
     * @param numeroDeCriaturas O número de criaturas para iniciar a simulação.
     * @param userId O ID do usuário que está executando a simulação.
     * @return O estado final do Horizonte, quando a condição de término da
     * simulação é atingida.
     * @throws IllegalArgumentException Se o 'numeroDeCriaturas' for inválido ou
     * se o 'userId' for nulo ou inválido.
     * @pre O 'numeroDeCriaturas' deve estar dentro do intervalo válido e o
     * 'userId' deve corresponder a um usuário existente.
     * @post Uma simulação completa é executada até que uma condição de término
     * seja atingida. As estatísticas do usuário (pontuação, simulações) são
     * atualizadas. O objeto retornado representa o estado final da simulação.
     */
    Horizon runFullSimulation(int numeroDeCriaturas, Long userId);
}
