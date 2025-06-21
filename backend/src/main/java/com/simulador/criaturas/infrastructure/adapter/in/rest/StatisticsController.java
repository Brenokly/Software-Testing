package com.simulador.criaturas.infrastructure.adapter.in.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.simulador.criaturas.domain.port.in.StatisticsUseCase;
import com.simulador.criaturas.infrastructure.adapter.in.rest.dto.GlobalStatisticsDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsUseCase statisticsUseCase;

    @GetMapping
    public ResponseEntity<GlobalStatisticsDTO> getStatistics() {
        GlobalStatisticsDTO stats = statisticsUseCase.getGlobalStatistics();
        return ResponseEntity.ok(stats);
    }
}
