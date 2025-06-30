package com.simulador.criaturas.infrastructure.adapter.in.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;
    private UserResponseDTO user;
}
