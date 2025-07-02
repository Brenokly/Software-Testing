package com.simulador.criaturas.infrastructure.adapter.in.rest;

import java.security.Principal;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.simulador.criaturas.domain.model.User;
import com.simulador.criaturas.domain.port.in.UserUseCase;
import com.simulador.criaturas.infrastructure.adapter.in.rest.dto.LoginResponseDTO;
import com.simulador.criaturas.infrastructure.adapter.in.rest.dto.UserRequestDTO;
import com.simulador.criaturas.infrastructure.adapter.in.rest.dto.UserResponseDTO;
import com.simulador.criaturas.infrastructure.adapter.in.rest.mapper.UserMapper;
import com.simulador.criaturas.infrastructure.config.security.JwtService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserUseCase userUseCase;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager; // Injete o AuthenticationManager

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO requestDTO) {

        User newUser = userUseCase.registerNewUser(requestDTO.getLogin(), requestDTO.getPassword(), requestDTO.getAvatarId());

        UserResponseDTO responseDTO = userMapper.toResponseDto(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody UserRequestDTO requestDTO) {
        // O AuthenticationManager usa o Spring Security para verificar o usuário
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDTO.getLogin(),
                        requestDTO.getPassword()
                )
        );

        Optional<User> userOptional = userUseCase.authenticateUser(requestDTO.getLogin(), requestDTO.getPassword());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Mapeia o usuário do domínio para o DTO de resposta
            UserResponseDTO userResponseDTO = userMapper.toResponseDto(user);

            // Gera o token
            String token = jwtService.generateToken(user.getLogin());

            // Cria o DTO de resposta final com o token E os dados do usuário
            LoginResponseDTO loginResponse = new LoginResponseDTO(token, userResponseDTO);

            return ResponseEntity.ok(loginResponse);
        } else {
            throw new RuntimeException("Login ou senha inválidos.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        userUseCase.deleteUser(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
