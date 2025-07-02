import apiClient from '../api/apiClient';
import { HorizonDTO } from '../types/simulationTypes';

export const startNewSimulationService = async (amount: number): Promise<HorizonDTO> => {
  const response = await apiClient.post<HorizonDTO>('/simulacao/iniciar', null, {
    params: { numeroDeCriaturas: amount },
  });

  return response.data;
};

export const runNextIterationService = async (currentState: HorizonDTO): Promise<HorizonDTO> => {
  const response = await apiClient.post<HorizonDTO>('/simulacao/iterar', currentState);
  return response.data;
};

export const runFullSimulationService = async (amount: number): Promise<HorizonDTO> => {
    const response = await apiClient.post<HorizonDTO>('/simulacao/executar-completa', null, {
        params: { numeroDeCriaturas: amount },
    });
    return response.data;
};