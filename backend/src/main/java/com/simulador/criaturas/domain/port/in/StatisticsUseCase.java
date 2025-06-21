package com.simulador.criaturas.domain.port.in;

import com.simulador.criaturas.infrastructure.adapter.in.rest.dto.GlobalStatisticsDTO;

public interface StatisticsUseCase {

    /**
     * Calcula e retorna as estatísticas globais de simulação do sistema.
     *
     * @return um DTO contendo as estatísticas agregadas.
     */
    GlobalStatisticsDTO getGlobalStatistics();
}
