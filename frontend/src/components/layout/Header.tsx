"use client";

import {
  clearAuthData,
  getToken,
  getUserData,
} from "@/utils/services/tokenManager";
import { UserData } from "@/utils/types/authTypes";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useEffect, useState } from "react";

export const Header = () => {
  // Usamos null como estado inicial para saber que a verificação ainda não ocorreu
  const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);
  const [userData, setUserData] = useState<UserData | null>(null);
  const router = useRouter();
  const pathname = usePathname(); // Hook para saber a rota atual

  useEffect(() => {
    // Esta função verifica o estado de autenticação no cliente
    const checkAuthStatus = () => {
      const token = getToken();
      const user = getUserData();
      setIsAuthenticated(!!token);
      setUserData(user);
    };

    checkAuthStatus();

    // Adiciona um listener para o evento de storage, para que se o usuário
    // logar ou deslogar em outra aba, esta aba se atualize.
    window.addEventListener("storage", checkAuthStatus);
    return () => {
      window.removeEventListener("storage", checkAuthStatus);
    };
  }, [pathname]); // Re-executa a verificação sempre que a rota muda

  const handleLogout = () => {
    clearAuthData(); // Limpa o token e dados do usuário
    setIsAuthenticated(false);
    setUserData(null);
    // Dispara o evento de storage para atualizar outras abas
    window.dispatchEvent(new Event("storage"));
    router.push("/login");
  };

  // Enquanto o estado de autenticação está sendo verificado, não mostramos nada
  // para evitar o "piscar" dos botões.
  if (isAuthenticated === null) {
    return <header className="h-[80px] w-full bg-[#3B82F6]"></header>; // Placeholder com a mesma altura
  }

  return (
    <header className="fixed top-0 left-0 w-full bg-[#3B82F6] bg-opacity-90 border-b-4 border-black p-4 z-50 shadow-lg backdrop-blur-sm">
      <nav className="container mx-auto flex justify-between items-center">
        <Link
          href="/"
          className="text-xl md:text-2xl text-white font-bold tracking-wider"
          style={{
            fontFamily: '"Press Start 2P", cursive',
            textShadow: "2px 2px #222",
          }}
        >
          Criaturas
        </Link>
        <div className="flex items-center space-x-2 md:space-x-4">
          {isAuthenticated ? (
            <>
              {/* Mostra os dados do usuário logado */}
              <div className="hidden md:flex items-center gap-2 text-white font-bold">
                <span>{userData?.login}</span>
                <span className="text-yellow-300">
                  | 💰 {userData?.pontuation}
                </span>
              </div>
              <Link
                href="/simulacao"
                className="text-white hover:text-yellow-300 transition-colors duration-200"
              >
                Simulação
              </Link>
              <Link
                href="/statistics"
                className="text-white hover:text-yellow-300 transition-colors duration-200"
              >
                Ranking
              </Link>
              <button
                onClick={handleLogout}
                className="px-3 py-2 bg-red-600 text-white border-2 border-black rounded-md hover:bg-red-700 shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] hover:translate-x-[-2px] hover:translate-y-[-2px] transition-all"
              >
                Sair
              </button>
            </>
          ) : (
            <>
              <Link
                href="/login"
                className="px-3 py-2 bg-green-500 text-white border-2 border-black rounded-md hover:bg-green-600 shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] hover:translate-x-[-2px] hover:translate-y-[-2px] transition-all"
              >
                Login
              </Link>
              <Link
                href="/register"
                className="hidden sm:block px-3 py-2 bg-blue-500 text-white border-2 border-black rounded-md hover:bg-blue-600 shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] hover:translate-x-[-2px] hover:translate-y-[-2px] transition-all"
              >
                Criar Conta
              </Link>
            </>
          )}
        </div>
      </nav>
    </header>
  );
};
