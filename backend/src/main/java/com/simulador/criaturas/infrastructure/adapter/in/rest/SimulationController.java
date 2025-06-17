package com.simulador.criaturas.infrastructure.adapter.in;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.simulador.criaturas.infrastructure.adapter.in.rest.dto.IterationStatusDTO;
import com.simulador.criaturas.infrastructure.adapter.in.rest.dto.SimulationRequestDTO;
import com.simulador.criaturas.service.SimulationManager;
import com.simulador.criaturas.service.SimulationService;

// Não sabia se era necessário testar o controller

@CrossOrigin
@RestController
@RequestMapping(value = "/api/simulacao", produces = MediaType.APPLICATION_JSON_VALUE)
public class SimulationController {

    private final SimulationManager simulationManager;

    @Autowired
    public SimulationController(SimulationManager simulationManager) {
        this.simulationManager = simulationManager;
    }

    @PostMapping("/iniciar")
    public IterationStatusDTO iniciarSimulacao(@Validated @RequestBody SimulationRequestDTO request) {
        return simulationManager.iniciarSimulacao(request.getQuantidade());
    }

    @PostMapping("/iterar")
    public IterationStatusDTO iterar() {
        return getService().iterate();
    }

    @GetMapping("/criatura-atual")
    public int getCriaturaAtual() {
        return getService().getCurrentCreatureId();
    }

    @PostMapping("/resetar")
    public IterationStatusDTO resetarSimulacao() {
        return getService().resetSimulation();
    }

    @GetMapping("/finalizar")
    public IterationStatusDTO finalizarSimulacao() {
        return getService().finishSimulation();
    }

    @GetMapping("/status")
    public IterationStatusDTO getStatus() {
        return getService().getIterationStatus();
    }

    private SimulationService getService() {
        return simulationManager.getSimulacao();
    }
}