package com.simulador.criaturas.infrastructure.adapter.in.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.simulador.criaturas.domain.model.Horizon;
import com.simulador.criaturas.domain.port.in.SimulacaoUseCase;
import com.simulador.criaturas.infrastructure.adapter.in.rest.dto.HorizonDTO;
import com.simulador.criaturas.infrastructure.adapter.in.rest.mapper.HorizonMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/simulacao")
@RequiredArgsConstructor
public class SimulacaoController {

    // O Controller depende da Porta de Entrada e agora do Mapper
    private final SimulacaoUseCase simulacaoUseCase;
    private final HorizonMapper horizonMapper;

    @PostMapping("/iniciar")
    public HorizonDTO iniciar(@RequestParam int numeroDeCriaturas) {
        Horizon horizonDominio = simulacaoUseCase.initNewSimulation(numeroDeCriaturas);
        return horizonMapper.toDto(horizonDominio);
    }

    @PostMapping("/iterar")
    public HorizonDTO iterar(@RequestBody HorizonDTO estadoAtualDTO, @RequestParam Long userId) {
        Horizon horizonDominio = horizonMapper.toDomain(estadoAtualDTO);
        Horizon novoHorizonDominio = simulacaoUseCase.runNextSimulation(horizonDominio, userId);
        return horizonMapper.toDto(novoHorizonDominio);
    }

    @PostMapping("/executar-completa")
    public HorizonDTO executarCompleta(@RequestParam int numeroDeCriaturas, @RequestParam Long userId) {
        Horizon horizonDominio = simulacaoUseCase.runFullSimulation(numeroDeCriaturas, userId);
        return horizonMapper.toDto(horizonDominio);
    }
}
