import axios, { AxiosError } from 'axios';
import { clearAuthData, getToken, isBrowser } from '../services/tokenManager';

const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor de Requisição: Adiciona o token em cada chamada
apiClient.interceptors.request.use(
  (config) => {
    const token = getToken();
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor de Resposta: Trata erros de autenticação
apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      if (isBrowser() && window.location.pathname !== '/login') {
        clearAuthData();
        alert('Sua sessão expirou. Por favor, faça o login novamente.');
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default apiClient;