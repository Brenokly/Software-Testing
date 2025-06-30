"use client";

import { loginUserService } from "@/utils/services/authService";
import { saveAuthData } from "@/utils/services/tokenManager";
import {
  LoginFormInputs,
  loginSchema,
} from "@/utils/validators/authValidators";
import { zodResolver } from "@hookform/resolvers/zod";
import { AxiosError } from "axios";
import Link from "next/link"; // Importa o Link para navegação
import { useRouter } from "next/navigation";
import { useState } from "react";
import { SubmitHandler, useForm } from "react-hook-form";
import { FaLock, FaUser } from "react-icons/fa"; // Ícones para os campos

export const LoginForm = () => {
  const [apiError, setApiError] = useState<string | null>(null);
  const router = useRouter();
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormInputs>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit: SubmitHandler<LoginFormInputs> = async (data) => {
    setApiError(null);
    try {
      const loginResponse = await loginUserService(data);
      saveAuthData(loginResponse);
      router.push("/simulacao");
    } catch (error) {
      if (error instanceof AxiosError && error.response) {
        setApiError(error.response.data.message || "Login ou senha inválidos.");
      } else if (error instanceof Error) {
        setApiError(error.message);
      } else {
        setApiError("Ocorreu um erro inesperado.");
      }
    }
  };

  return (
    // Usando a textura de madeira da sua imagem 'Extra_Unfinished4.png'
    // Certifique-se de recortar a textura da porta e salvá-la como 'wood-panel.png'
    <div
      className="w-full max-w-md p-8 space-y-6 bg-cover bg-center border-8 border-[#4a2c2a] rounded-lg shadow-2xl"
      style={{ backgroundImage: "url('/images/wood-panel.png')" }}
    >
      <h1
        className="text-4xl text-yellow-300 text-center"
        style={{
          fontFamily: '"Press Start 2P", cursive',
          textShadow: "3px 3px #000",
        }}
      >
        Entrar
      </h1>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        {/* Input de Login com ícone */}
        <div className="relative">
          <FaUser className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
          <input
            {...register("login")}
            placeholder="Nome de Aventureiro"
            className="w-full pl-10 pr-3 py-2 bg-[#2a1a1a] text-white border-2 border-black rounded-md focus:border-yellow-400 focus:ring-yellow-400 outline-none"
          />
        </div>
        {errors.login && (
          <p className="text-red-400 -mt-2 text-sm">{errors.login.message}</p>
        )}

        {/* Input de Senha com ícone */}
        <div className="relative">
          <FaLock className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
          <input
            type="password"
            {...register("password")}
            placeholder="Palavra Secreta"
            className="w-full pl-10 pr-3 py-2 bg-[#2a1a1a] text-white border-2 border-black rounded-md focus:border-yellow-400 focus:ring-yellow-400 outline-none"
          />
        </div>
        {errors.password && (
          <p className="text-red-400 -mt-2 text-sm">
            {errors.password.message}
          </p>
        )}

        {apiError && (
          <p className="text-red-400 text-center font-bold">{apiError}</p>
        )}

        {/* Botão de Entrar */}
        <button
          type="submit"
          disabled={isSubmitting}
          className="w-full p-3 bg-green-600 text-white border-2 border-black rounded-md hover:bg-green-700 disabled:bg-gray-500 shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] hover:translate-x-[-2px] hover:translate-y-[-2px] active:shadow-none active:translate-x-[4px] active:translate-y-[4px] transition-all font-bold"
          style={{ fontFamily: '"Press Start 2P", cursive' }}
        >
          {isSubmitting ? "Verificando..." : "Entrar na Guilda"}
        </button>
      </form>

      {/* Link para a página de Registro */}
      <p className="text-center text-white text-sm">
        Não tem uma conta?{" "}
        <Link
          href="/register"
          className="font-bold text-yellow-300 hover:underline"
        >
          Crie uma aqui!
        </Link>
      </p>
    </div>
  );
};
