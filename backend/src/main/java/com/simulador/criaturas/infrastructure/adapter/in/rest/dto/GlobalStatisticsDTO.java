package com.simulador.criaturas.infrastructure.adapter.in.rest.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalStatisticsDTO {

    private long totalSimulationsRun;
    private double overallSuccessRate;
    private List<UserStatisticsDTO> userStatistics;
}
