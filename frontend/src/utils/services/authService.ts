import apiClient from '../api/apiClient';
import { AuthRequest, LoginResponse, UserData } from '../types/authTypes';

export const registerUserService = async (data: AuthRequest): Promise<UserData> => {
  const response = await apiClient.post<UserData>('/users/register', data);
  return response.data;
};

export const loginUserService = async (data: AuthRequest): Promise<LoginResponse> => {
  const response = await apiClient.post<LoginResponse>('/users/login', data);
  return response.data;
};

export const deleteUserAccountService = async (userId: number): Promise<void> => {
  await apiClient.delete(`/users/${userId}`);
};