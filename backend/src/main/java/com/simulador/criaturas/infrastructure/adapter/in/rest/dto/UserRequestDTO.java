package com.simulador.criaturas.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para receber dados de criação ou login de usuário. Usamos anotações de
 * validação aqui para rejeitar requisições inválidas na camada mais externa
 * possível.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    @NotBlank(message = "O login não pode ser vazio.")
    private String login;

    @NotBlank(message = "A senha não pode ser vazia.")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
    private String password;
}
