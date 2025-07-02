package com.simulador.criaturas.infrastructure.adapter.in.rest.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalStatisticsDTO {

    // Estatísticas globais
    private long totalSimulationsRun;
    private double overallSuccessRate;

    // Dados da página atual
    private List<UserStatisticsDTO> userRankingPage;

    // Informações de paginação
    private int currentPage;
    private int totalPages;
    private long totalUsers;
}
