package com.simulador.criaturas.application;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        public GlobalStatisticsDTO getGlobalStatistics(Pageable pageable) {
                Sort sort = Sort.by(Sort.Direction.DESC, "pontuation") // Critério principal: maior pontuação primeiro
                                .and(Sort.by(Sort.Direction.ASC, "login")); // Desempate: ordem alfabética do login

                // 2. Crie um novo objeto Pageable combinando a paginação original com a nova
                // ordenação
                Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

                // 3. Use o Pageable ORDENADO na sua busca no banco de dados
                Page<User> userPage = userRepository.findAll(sortedPageable);

                // 2. Mapeia a PÁGINA de usuários para uma LISTA de DTOs
                List<UserStatisticsDTO> userStatsList = userPage.getContent().stream()
                                .map(this::mapToUserStatisticsDto)
                                .toList();

                // 3. Busca os totais globais com as queries eficientes
                long totalSimulations = userRepository.countTotalSimulations();
                long totalSuccesses = userRepository.countTotalSuccesses();
                double overallRate = (totalSimulations == 0) ? 0.0 : (double) totalSuccesses / totalSimulations;

                // 4. Monta o DTO de resposta final com os dados globais e os dados da página
                return new GlobalStatisticsDTO(
                                totalSimulations,
                                overallRate,
                                userStatsList,
                                userPage.getNumber(), // número da página atual (0-indexado)
                                userPage.getTotalPages(), // total de páginas
                                userPage.getTotalElements() // total de usuários no banco
                );
        }

        private UserStatisticsDTO mapToUserStatisticsDto(User user) {
                return new UserStatisticsDTO(
                                user.getLogin(),
                                user.getPontuation(),
                                user.getSimulationsRun(),
                                user.getAverageSuccessRate());
        }
}
