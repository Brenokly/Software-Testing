package com.simulador.criaturas.infrastructure.adapter.in.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para enviar dados de um usuário como resposta da API. Note a ausência do
 * campo 'password' por segurança.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String login;
    private int avatarId;
    private int pontuation;
}
