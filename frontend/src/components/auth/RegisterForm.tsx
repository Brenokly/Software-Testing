"use client";

import { AvatarPicker } from "@/components/auth/AvatarPicker";
import { registerUserService } from "@/utils/services/authService";
import {
  RegisterFormInputs,
  registerSchema,
} from "@/utils/validators/authValidators";
import { zodResolver } from "@hookform/resolvers/zod";
import { AxiosError } from "axios";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { SubmitHandler, useForm } from "react-hook-form";
import { FaLock, FaUser } from "react-icons/fa";

export const RegisterForm = () => {
  const [apiError, setApiError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const router = useRouter();

  // O useForm agora gerencia o estado do avatarId
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    setValue,
    watch,
  } = useForm<RegisterFormInputs>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      avatarId: 1,
    },
  });

  // Usamos 'watch' para que o componente AvatarPicker saiba qual avatar está selecionado
  const selectedAvatar = watch("avatarId");

  const onSubmit: SubmitHandler<RegisterFormInputs> = async (data) => {
    setApiError(null);
    setSuccessMessage(null);
    try {
      // O 'data' agora já contém o avatarId selecionado pelo usuário
      await registerUserService(data);
      setSuccessMessage(
        "Conta criada com sucesso! Redirecionando para o login..."
      );

      setTimeout(() => {
        router.push("/login");
      }, 2000);
    } catch (error) {
      if (error instanceof AxiosError && error.response) {
        setApiError(
          error.response.data.message || "Não foi possível criar a conta."
        );
      } else if (error instanceof Error) {
        setApiError(error.message);
      } else {
        setApiError("Ocorreu um erro inesperado.");
      }
    }
  };

  return (
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
        Criar Conta
      </h1>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        {/* Input de Login */}
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

        <AvatarPicker
          selectedValue={selectedAvatar}
          // Passamos a função setValue do react-hook-form para o componente filho
          onSelect={(id) => setValue("avatarId", id, { shouldValidate: true })}
        />

        {errors.avatarId && (
          <p className="text-red-400 -mt-2 text-sm">
            {errors.avatarId.message}
          </p>
        )}

        {/* Input de Senha */}
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

        {/* Input de Confirmação de Senha */}
        <div className="relative">
          <FaLock className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
          <input
            type="password"
            {...register("confirmPassword")}
            placeholder="Repita a Palavra Secreta"
            className="w-full pl-10 pr-3 py-2 bg-[#2a1a1a] text-white border-2 border-black rounded-md focus:border-yellow-400 focus:ring-yellow-400 outline-none"
          />
        </div>
        {errors.confirmPassword && (
          <p className="text-red-400 -mt-2 text-sm">
            {errors.confirmPassword.message}
          </p>
        )}

        {/* Mensagens de Erro ou Sucesso */}
        {apiError && (
          <p className="text-red-400 text-center font-bold">{apiError}</p>
        )}
        {successMessage && (
          <p className="text-green-400 text-center font-bold">
            {successMessage}
          </p>
        )}

        {/* Botão de Registrar */}
        <button
          type="submit"
          disabled={isSubmitting}
          className="w-full p-3 bg-blue-600 text-white border-2 border-black rounded-md hover:bg-blue-700 disabled:bg-gray-500 shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] hover:translate-x-[-2px] hover:translate-y-[-2px] active:shadow-none active:translate-x-[4px] active:translate-y-[4px] transition-all font-bold"
          style={{ fontFamily: '"Press Start 2P", cursive' }}
        >
          {isSubmitting ? "Criando..." : "Juntar-se à Guilda"}
        </button>
      </form>

      <p className="text-center text-white text-sm">
        Já tem uma conta?{" "}
        <Link
          href="/login"
          className="font-bold text-yellow-300 hover:underline"
        >
          Entre aqui!
        </Link>
      </p>
    </div>
  );
};
