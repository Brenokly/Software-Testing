package com.simulador.criaturas.infrastructure.adapter.in.rest;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.simulador.criaturas.utils.SimulationStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
    public HorizonDTO iniciar(@RequestParam @Min(1) @Max(10) int numeroDeCriaturas, Principal principal) {
        Horizon horizonDominio = simulacaoUseCase.initNewSimulation(numeroDeCriaturas);
        return horizonMapper.toDto(horizonDominio);
    }

    /**
     * Executa uma única iteração de uma simulação para o usuário autenticado.
     * Inclui uma validação "Fail-Fast" para rejeitar iterações em simulações já
     * concluídas.
     */
    @PostMapping("/iterar")
    public ResponseEntity<?> iterar(@Valid @RequestBody HorizonDTO estadoAtualDTO, Principal principal) {

        // Verifico o status da simulação ANTES de fazer qualquer coisa.
        if (estadoAtualDTO.getStatus() != SimulationStatus.RUNNING) {
            String errorMessage = "Não é possível iterar uma simulação que já foi concluída com o status: " + estadoAtualDTO.getStatus();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        }

        // Se a validação passar, o fluxo continua normalmente...
        User user = userUseCase.findUserByLogin(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado."));

        Horizon horizonDominio = horizonMapper.toDomain(estadoAtualDTO);
        Horizon novoHorizonDominio = simulacaoUseCase.runNextSimulation(horizonDominio, user.getId());

        // Retornamos o DTO mapeado.
        return ResponseEntity.ok(horizonMapper.toDto(novoHorizonDominio));
    }

    /**
     * Executa uma simulação completa do início ao fim para o usuário
     * autenticado.
     */
    @PostMapping("/executar-completa")
    public HorizonDTO executarCompleta(@RequestParam @Min(1) @Max(10) int numeroDeCriaturas, Principal principal) {
        User user = userUseCase.findUserByLogin(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado."));

        Horizon horizonDominio = simulacaoUseCase.runFullSimulation(numeroDeCriaturas, user.getId());
        return horizonMapper.toDto(horizonDominio);
    }
}
