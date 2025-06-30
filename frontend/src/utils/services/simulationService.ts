import apiClient from '../api/apiClient';
import { HorizonDTO } from '../types/simulationTypes';

export const startNewSimulationService = async (creatureCount: number): Promise<HorizonDTO> => {
  const response = await apiClient.post<HorizonDTO>('/simulacao/iniciar', null, {
    params: { amount: creatureCount },
  });
  return response.data;
};

export const runNextIterationService = async (currentState: HorizonDTO): Promise<HorizonDTO> => {
  const response = await apiClient.post<HorizonDTO>('/simulacao/iterar', currentState);
  return response.data;
};

export const runFullSimulationService = async (creatureCount: number): Promise<HorizonDTO> => {
    const response = await apiClient.post<HorizonDTO>('/simulacao/executar-completa', null, {
        params: { amount: creatureCount },
    });
    return response.data;
};