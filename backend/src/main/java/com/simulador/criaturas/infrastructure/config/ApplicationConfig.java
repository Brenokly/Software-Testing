package com.simulador.criaturas.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.simulador.criaturas.domain.port.out.RandomPort;
import com.simulador.criaturas.domain.service.Simulation;
import com.simulador.criaturas.infrastructure.adapter.out.RandomGeneratorAdapter;

/**
 * Classe de configuração para declarar Beans que não podem ser anotados
 * diretamente, como os componentes puros da camada de domínio.
 */
@Configuration
public class ApplicationConfig {

    /**
     * Declara um Bean para a nossa implementação da porta RandomPort. O Spring
     * irá gerenciar este objeto.
     */
    @Bean
    public RandomPort randomPort() {
        return new RandomGeneratorAdapter();
    }

    /**
     * Declara um Bean para o nosso serviço de domínio Simulation.
     *
     * @param randomPort O Spring é inteligente. Ao ver que este método precisa
     * de um RandomPort, ele irá procurar por um Bean desse tipo (que acabamos
     * de criar acima) e o injetará automaticamente aqui.
     * @return Uma instância gerenciada de Simulation.
     */
    @Bean
    public Simulation simulation(RandomPort randomPort) {
        return new Simulation(randomPort);
    }
}
