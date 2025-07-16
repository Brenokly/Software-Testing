"use client";

import { getAvatarById } from "@/utils/services/avatarService"; // Importa nossa nova função
import {
  clearAuthData,
  getToken,
  getUserData,
} from "@/utils/services/tokenManager";
import { UserData } from "@/utils/types/authTypes";
import Image from "next/image"; // Importa o componente Image do Next.js
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useEffect, useState } from "react";

export const Header = () => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);
  const [userData, setUserData] = useState<UserData | null>(null);
  const [avatarUrl, setAvatarUrl] = useState<string>("/avatares/1.png"); // Um avatar padrão enquanto carrega
  const router = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    const checkAuthStatus = () => {
      const token = getToken();
      const user = getUserData();
      setIsAuthenticated(!!token);
      setUserData(user);

      // Se o usuário existir, define a URL do avatar correspondente
      if (user) {
        setAvatarUrl(getAvatarById(user.avatarId));
      }
    };

    checkAuthStatus();

    window.addEventListener("storage", checkAuthStatus);
    return () => {
      window.removeEventListener("storage", checkAuthStatus);
    };
  }, [pathname]);

  const handleLogout = () => {
    clearAuthData();
    setIsAuthenticated(false);
    setUserData(null);
    window.dispatchEvent(new Event("storage"));
    router.push("/login");
  };

  if (isAuthenticated === null) {
    return <header className="h-[80px] w-full bg-[#3B82F6]"></header>;
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
          {isAuthenticated && userData ? (
            <>
              {/* Perfil do Usuário com Avatar */}
              <div className="flex items-center gap-3">
                <Link
                  href="/perfil"
                  data-testid="profile-link"
                  className="flex items-center gap-3 cursor-pointer"
                >
                  <Image
                    src={avatarUrl}
                    alt={`Avatar de ${userData.login}`}
                    width={48}
                    height={48}
                    className="rounded-full border-2 border-yellow-300 object-cover"
                  />
                  <div className="hidden md:flex flex-col text-white font-bold leading-tight">
                    <span>{userData.login}</span>
                  </div>
                </Link>
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
              {/* Botões de Login e Registro */}
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
