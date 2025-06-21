package com.simulador.criaturas.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.simulador.criaturas.domain.model.User;
import com.simulador.criaturas.domain.port.in.StatisticsUseCase;
import com.simulador.criaturas.domain.port.out.UserRepositoryPort;
import com.simulador.criaturas.infrastructure.adapter.in.rest.dto.GlobalStatisticsDTO;
import com.simulador.criaturas.infrastructure.adapter.in.rest.dto.UserStatisticsDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsService implements StatisticsUseCase {

    private final UserRepositoryPort userRepository;

    @Override
    public GlobalStatisticsDTO getGlobalStatistics() {
        // 1. Busca todos os usuários do repositório
        List<User> allUsers = userRepository.findAll();

        if (allUsers.isEmpty()) {
            return new GlobalStatisticsDTO(0, 0.0, List.of());
        }

        // 2. Mapeia cada usuário para seu DTO de estatísticas individuais
        List<UserStatisticsDTO> userStatsList = allUsers.stream()
                .map(this::mapToUserStatisticsDto)
                .toList();

        // 3. Calcula as estatísticas globais
        long totalSimulations = userStatsList.stream()
                .mapToLong(UserStatisticsDTO::getSimulationsRun)
                .sum();

        long totalSuccesses = userStatsList.stream()
                .mapToLong(UserStatisticsDTO::getScore)
                .sum();

        double overallRate = (totalSimulations == 0) ? 0.0 : (double) totalSuccesses / totalSimulations;

        return new GlobalStatisticsDTO(totalSimulations, overallRate, userStatsList);
    }

    private UserStatisticsDTO mapToUserStatisticsDto(User user) {
        return new UserStatisticsDTO(
                user.getLogin(),
                user.getPontuation(),
                user.getSimulationsRun(),
                user.getAverageSuccessRate()
        );
    }
}
