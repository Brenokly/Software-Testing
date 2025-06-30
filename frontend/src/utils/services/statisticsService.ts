import apiClient from '../api/apiClient';
import { GlobalStatisticsDTO } from '../types/statisticsTypes';

export const getGlobalStatisticsService = async (): Promise<GlobalStatisticsDTO> => {
  const response = await apiClient.get<GlobalStatisticsDTO>('/statistics');
  return response.data;
};