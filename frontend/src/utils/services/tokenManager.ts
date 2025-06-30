import { LoginResponse, UserData } from '../types/authTypes';

const TOKEN_KEY = 'jwt_token';
const USER_DATA_KEY = 'user_data';

export const isBrowser = (): boolean => typeof window !== 'undefined';

export const saveAuthData = (loginResponse: LoginResponse): void => {
    if (isBrowser()) {
        localStorage.setItem(TOKEN_KEY, loginResponse.token);
        localStorage.setItem(USER_DATA_KEY, JSON.stringify(loginResponse.user));
    }
};

export const getToken = (): string | null => {
    return isBrowser() ? localStorage.getItem(TOKEN_KEY) : null;
};

export const getUserData = (): UserData | null => {
    if (!isBrowser()) return null;
    const data = localStorage.getItem(USER_DATA_KEY);
    try {
        return data ? JSON.parse(data) : null;
    } catch (e) {
        console.error("Failed to parse user data from localStorage", e);
        return null;
    }
}

export const clearAuthData = (): void => {
    if (isBrowser()) {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_DATA_KEY);
    }
};