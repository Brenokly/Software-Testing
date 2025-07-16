package com.simulador.criaturas.infrastructure.adapter.in.rest;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    /**
     * Retorna as estatísticas globais. O endpoint é protegido e requer que o
     * usuário esteja autenticado para acessá-lo.
     */
    @GetMapping
    public ResponseEntity<GlobalStatisticsDTO> getStatistics(
            @PageableDefault(size = 5, sort = "pontuation", direction = Sort.Direction.DESC) Pageable pageable) {
        GlobalStatisticsDTO stats = statisticsUseCase.getGlobalStatistics(pageable);
        return ResponseEntity.ok(stats);
    }
}
