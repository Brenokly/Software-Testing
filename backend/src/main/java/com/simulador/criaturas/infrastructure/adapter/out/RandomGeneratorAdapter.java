package com.simulador.criaturas.infrastructure.adapter.out;

import org.springframework.stereotype.Component;

import com.simulador.criaturas.domain.port.out.RandomPort;

@Component
public class RandomGeneratorAdapter implements RandomPort {

    /**
     * @return um double aleatório no intervalo [-1, 1].
     */
    @Override
    public double nextFactor() {
        return -1 + Math.random() * 2.0000001;
    }
}
