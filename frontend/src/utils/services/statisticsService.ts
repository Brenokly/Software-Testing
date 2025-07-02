import apiClient from '../api/apiClient';
import { GlobalStatisticsDTO } from '../types/statisticsTypes';

export const getGlobalStatisticsService = async (page: number, size: number): Promise<GlobalStatisticsDTO> => {
  const response = await apiClient.get<GlobalStatisticsDTO>('/statistics', {
    params: {
      page,
      size,
      sort: 'pontuation,desc'
    }
  });
  return response.data;
};