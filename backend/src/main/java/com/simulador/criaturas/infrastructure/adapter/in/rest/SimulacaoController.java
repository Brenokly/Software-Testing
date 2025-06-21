package com.simulador.criaturas.infrastructure.adapter.in.rest;

import java.security.Principal;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.simulador.criaturas.domain.model.Horizon;
import com.simulador.criaturas.domain.model.User;
import com.simulador.criaturas.domain.port.in.SimulacaoUseCase;
import com.simulador.criaturas.domain.port.in.UserUseCase;
import com.simulador.criaturas.infrastructure.adapter.in.rest.dto.HorizonDTO;
import com.simulador.criaturas.infrastructure.adapter.in.rest.mapper.HorizonMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/simulacao")
@RequiredArgsConstructor
public class SimulacaoController {

    private final SimulacaoUseCase simulacaoUseCase;
    private final UserUseCase userUseCase;
    private final HorizonMapper horizonMapper;

    /**
     * Inicia uma nova simulação. O endpoint é protegido, garantindo que apenas
     * usuários logados possam iniciar simulações.
     */
    @PostMapping("/iniciar")
    public HorizonDTO iniciar(@RequestParam int numeroDeCriaturas, Principal principal) {
        Horizon horizonDominio = simulacaoUseCase.initNewSimulation(numeroDeCriaturas);
        return horizonMapper.toDto(horizonDominio);
    }

    /**
     * Executa uma única iteração de uma simulação para o usuário autenticado.
     */
    @PostMapping("/iterar")
    public HorizonDTO iterar(@RequestBody HorizonDTO estadoAtualDTO, Principal principal) {
        User user = userUseCase.findUserByLogin(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado."));

        Horizon horizonDominio = horizonMapper.toDomain(estadoAtualDTO);
        Horizon novoHorizonDominio = simulacaoUseCase.runNextSimulation(horizonDominio, user.getId());
        return horizonMapper.toDto(novoHorizonDominio);
    }

    /**
     * Executa uma simulação completa do início ao fim para o usuário
     * autenticado.
     */
    @PostMapping("/executar-completa")
    public HorizonDTO executarCompleta(@RequestParam int numeroDeCriaturas, Principal principal) {
        User user = userUseCase.findUserByLogin(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado."));

        Horizon horizonDominio = simulacaoUseCase.runFullSimulation(numeroDeCriaturas, user.getId());
        return horizonMapper.toDto(horizonDominio);
    }
}
