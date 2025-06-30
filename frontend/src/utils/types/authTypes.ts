// DTO para a requisição de login/registro que o frontend envia
export interface AuthRequest {
  login: string;
  password?: string;
}

// A resposta que o backend envia no SUCESSO do login
export interface LoginResponse {
  token: string;
  user: UserData;
}

// Como representamos os dados do usuário no frontend
export interface UserData {
  id: number;
  login: string;
  avatarId: number;
  pontuation: number;
  simulationsRun: number;
}
