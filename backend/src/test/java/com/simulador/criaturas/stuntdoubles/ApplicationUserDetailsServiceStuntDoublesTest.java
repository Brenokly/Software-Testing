package com.simulador.criaturas.stuntdoubles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.simulador.criaturas.application.ApplicationUserDetailsService;
import com.simulador.criaturas.domain.model.User;
import com.simulador.criaturas.domain.port.out.UserRepositoryPort;

@ExtendWith(MockitoExtension.class)
public class ApplicationUserDetailsServiceStuntDoublesTest {

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private ApplicationUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserIsFound() {
        String login = "testuser";
        String encodedPassword = "encodedPassword123";
        User userDoDominio = new User(1L, login, encodedPassword, 1, 10, 20);

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(userDoDominio));

        UserDetails userDetailsResult = userDetailsService.loadUserByUsername(login);

        assertNotNull(userDetailsResult);
        assertEquals(userDoDominio.getLogin(), userDetailsResult.getUsername());
        assertEquals(userDoDominio.getPassword(), userDetailsResult.getPassword());
        assertTrue(userDetailsResult.getAuthorities().isEmpty());
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserIsNotFound() {
        String nonExistentLogin = "ghost";

        when(userRepository.findByLogin(nonExistentLogin)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(nonExistentLogin);
        });

        assertTrue(exception.getMessage().contains("Usuário não encontrado: " + nonExistentLogin));
    }
}
