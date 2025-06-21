package com.simulador.criaturas.infrastructure.adapter.in.rest;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.simulador.criaturas.domain.model.User;
import com.simulador.criaturas.domain.port.in.UserUseCase;
import com.simulador.criaturas.infrastructure.adapter.in.rest.dto.UserRequestDTO;
import com.simulador.criaturas.infrastructure.adapter.in.rest.dto.UserResponseDTO;
import com.simulador.criaturas.infrastructure.adapter.in.rest.mapper.UserMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserUseCase userUseCase;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registrar(@Valid @RequestBody UserRequestDTO requestDTO) {
        // A anotação @Valid ativa as validações que colocamos no DTO
        User novoUsuario = userUseCase.registerNewUser(requestDTO.getLogin(), requestDTO.getPassword());
        UserResponseDTO responseDTO = userMapper.toResponseDto(novoUsuario);
        // Retorna 201 Created, que é o status correto para criação de recurso
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserRequestDTO requestDTO) {
        // 1. Chama o caso de uso e obtém o Optional
        Optional<User> userOptional = userUseCase.authenticateUser(requestDTO.getLogin(), requestDTO.getPassword());

        // 2. Verifica o resultado
        if (userOptional.isPresent()) {
            // Caminho do Sucesso
            User user = userOptional.get();
            UserResponseDTO responseDTO = userMapper.toResponseDto(user);
            // Em um sistema real, aqui geraria e retornaria um Token JWT
            return ResponseEntity.ok(responseDTO);
        } else {
            // Caminho do Erro
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login ou senha inválidos.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userUseCase.deleteUser(id);
        // Retorna 204 No Content, o status correto para deleção bem-sucedida
        return ResponseEntity.noContent().build();
    }
}
