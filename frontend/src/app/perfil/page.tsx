"use client";

import { Header } from "@/components/layout/Header";
import { deleteUserAccountService } from "@/utils/services/authService";
import { clearAuthData, getUserData } from "@/utils/services/tokenManager";
import { UserData } from "@/utils/types/authTypes";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";

// --- NOVO COMPONENTE DE MODAL ---
// Criado aqui para simplicidade, mas poderia ser um componente reutilizável.
const ConfirmationModal = ({
  isOpen,
  onClose,
  onConfirm,
  title,
  message,
}: {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  title: string;
  message: string;
}) => {
  if (!isOpen) return null;

  return (
    // Overlay de fundo
    <div
      data-testid="confirmation-modal"
      className="fixed inset-0 bg-black/60 z-50 flex justify-center items-center"
    >
      {/* Caixa do Modal */}
      <div className="bg-white p-6 rounded-lg shadow-xl w-full max-w-sm">
        <h2 className="text-xl font-bold mb-4">{title}</h2>
        <p className="mb-6 text-gray-700">{message}</p>
        <div className="flex justify-end gap-4">
          <button
            onClick={onClose}
            data-testid="cancel-button"
            className="px-4 py-2 bg-gray-300 text-gray-800 rounded-md hover:bg-gray-400"
          >
            Cancelar
          </button>
          <button
            onClick={onConfirm}
            data-testid="confirm-delete-button"
            className="px-4 py-2 bg-red-600 text-white font-bold rounded-md hover:bg-red-700"
          >
            Sim, Excluir
          </button>
        </div>
      </div>
    </div>
  );
};

// --- COMPONENTE PRINCIPAL DA PÁGINA (MODIFICADO) ---
export default function ProfilePage() {
  const [userData, setUserData] = useState<UserData | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false); // Estado para controlar o modal
  const router = useRouter();

  useEffect(() => {
    const user = getUserData();
    if (!user) {
      router.push("/login");
    } else {
      setUserData(user);
    }
  }, [router]);

  // Esta função agora APENAS abre o modal de confirmação.
  const handleDeleteClick = () => {
    setIsModalOpen(true);
  };

  // Esta é a função que o modal vai chamar ao clicar em "Confirmar".
  const handleConfirmDeletion = async () => {
    if (userData) {
      try {
        await deleteUserAccountService(userData.id);
        // O alerta feio foi removido.
        clearAuthData();
        window.dispatchEvent(new Event("storage"));
        router.push("/");
      } catch (error) {
        console.error("Falha ao excluir a conta:", error);
        alert("Não foi possível excluir a conta. Tente novamente."); // Mantemos um alerta para erro inesperado.
      }
    }
    setIsModalOpen(false); // Fecha o modal após a ação
  };

  if (!userData) {
    return <p>Carregando perfil...</p>;
  }

  return (
    <>
      <Header />
      <main
        className="flex min-h-screen flex-col items-center justify-center p-4 pt-24 bg-cover bg-center"
        style={{
          backgroundImage: "url('/images/ceu.png')",
        }}
      >
        <div className="w-full max-w-md p-8 space-y-6 bg-white/90 rounded-lg shadow-xl">
          <h1 className="text-2xl font-bold text-center">Seu Perfil</h1>
          <div className="space-y-2">
            <p>
              <strong>Aventureiro:</strong> {userData.login}
            </p>
            <p>
              <strong>Pontuação:</strong> {userData.pontuation}
            </p>
            <p>
              <strong>Simulações Totais:</strong> {userData.simulationsRun}
            </p>
          </div>
          <button
            onClick={handleDeleteClick} // ATUALIZADO: Chama a função que abre o modal
            data-testid="delete-account-button"
            className="w-full p-3 bg-red-600 text-white font-bold rounded-md hover:bg-red-700"
          >
            Excluir Minha Conta
          </button>
        </div>
      </main>

      {/* O Modal é renderizado aqui quando o estado 'isModalOpen' é true */}
      <ConfirmationModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onConfirm={handleConfirmDeletion}
        title="Confirmar Exclusão de Conta"
        message="Você tem certeza que deseja excluir sua conta? Esta ação é irreversível!"
      />
    </>
  );
}
