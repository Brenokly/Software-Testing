package com.simulador.criaturas.infrastructure.adapter.in.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsDTO {

    private String login;
    private int score;          // Pontuação do usuário
    private int simulationsRun; // Número de simulações executadas pelo usuário
    private double successRate; // Taxa de sucesso do usuário (pontuação / simulações executadas)
}
