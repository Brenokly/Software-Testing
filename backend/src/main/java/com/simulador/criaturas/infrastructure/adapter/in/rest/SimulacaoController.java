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
        // 1. Serviço de aplicação retorna o objeto de DOMÍNIO
        Horizon horizonDominio = simulacaoUseCase.initNewSimulation(numeroDeCriaturas);
        // 2. Controller usa o Mapper para converter para DTO antes de enviar
        return horizonMapper.toDto(horizonDominio);
    }

    @PostMapping("/iterar")
    public HorizonDTO iterar(@RequestBody HorizonDTO estadoAtualDTO) {
        // 1. Controller recebe o DTO do frontend
        Horizon horizonDominio = horizonMapper.toDomain(estadoAtualDTO);

        // 2. Chama o caso de uso com o objeto de DOMÍNIO
        Horizon novoHorizonDominio = simulacaoUseCase.runNextSimulation(horizonDominio);

        // 3. Converte o resultado do DOMÍNIO de volta para DTO para a resposta
        return horizonMapper.toDto(novoHorizonDominio);
    }

    @PostMapping("/executar-completa")
    public HorizonDTO executarCompleta(@RequestParam int numeroDeCriaturas) {
        // 1. Serviço de aplicação retorna o objeto de DOMÍNIO
        Horizon horizonDominio = simulacaoUseCase.runFullSimulation(numeroDeCriaturas);
        // 2. Controller usa o Mapper para converter para DTO antes de enviar
        return horizonMapper.toDto(horizonDominio);
    }
}
