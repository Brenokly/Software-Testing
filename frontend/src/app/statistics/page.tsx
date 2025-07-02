"use client";

import { Header } from "@/components/layout/Header";
import { getGlobalStatisticsService } from "@/utils/services/statisticsService";
import {
  GlobalStatisticsDTO,
  UserStatisticsDTO,
} from "@/utils/types/statisticsTypes";
import { useEffect, useState } from "react";
import { GiLaurelsTrophy } from "react-icons/gi";

const RankingTable = ({
  users,
  currentPage,
  pageSize,
}: {
  users: UserStatisticsDTO[];
  currentPage: number;
  pageSize: number;
}) => (
  <div className="overflow-x-auto">
    <table className="min-w-full bg-[#2a1a1a] text-white rounded-lg">
      <thead>
        <tr className="bg-black/50 text-yellow-300 uppercase text-sm leading-normal">
          <th className="py-3 px-6 text-left">Pos.</th>
          <th className="py-3 px-6 text-left">Aventureiro</th>
          <th className="py-3 px-6 text-center">Pontuação</th>
          <th className="py-3 px-6 text-center">Simulações</th>
          <th className="py-3 px-6 text-center">Taxa de Sucesso</th>
        </tr>
      </thead>
      <tbody className="text-gray-200 text-sm font-light">
        {users.map((user, index) => (
          <tr
            key={user.login}
            className="border-b border-gray-700 hover:bg-yellow-900/50"
          >
            <td className="py-3 px-6 text-left whitespace-nowrap">
              {currentPage * pageSize + index + 1}º
            </td>
            <td className="py-3 px-6 text-left">{user.login}</td>
            <td className="py-3 px-6 text-center font-bold">{user.score}</td>
            <td className="py-3 px-6 text-center">{user.simulationsRun}</td>
            <td className="py-3 px-6 text-center">
              {(user.successRate * 100).toFixed(1)}%
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  </div>
);

// Componente para os Controles de Paginação
const PaginationControls = ({
  currentPage,
  totalPages,
  onPageChange,
}: {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}) => (
  <div className="flex justify-center items-center gap-4 mt-4">
    <button
      onClick={() => onPageChange(currentPage - 1)}
      disabled={currentPage === 0}
      className="px-4 py-2 bg-blue-600 text-white font-bold rounded disabled:bg-gray-500"
    >
      Anterior
    </button>
    <span className="text-white">
      Página {currentPage + 1} de {totalPages}
    </span>
    <button
      onClick={() => onPageChange(currentPage + 1)}
      disabled={currentPage + 1 >= totalPages}
      className="px-4 py-2 bg-blue-600 text-white font-bold rounded disabled:bg-gray-500"
    >
      Próximo
    </button>
  </div>
);

// Componente Principal da Página
export default function StatisticsPage() {
  const [stats, setStats] = useState<GlobalStatisticsDTO | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const pageSize = 5; // Quantos usuários por página

  useEffect(() => {
    const fetchStats = async () => {
      setIsLoading(true);
      try {
        const data = await getGlobalStatisticsService(currentPage, pageSize);
        setStats(data);
      } catch (error) {
        console.error("Falha ao buscar estatísticas:", error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchStats();
  }, [currentPage]); // Re-executa sempre que a página atual mudar

  return (
    <div
      className="w-screen min-h-screen flex flex-col bg-cover bg-center"
      style={{ backgroundImage: "url('/images/ceu.png')" }}
    >
      <Header />
      <main className="flex-1 flex flex-col items-center justify-center p-4 md:p-8">
        <div
          className="w-full max-w-4xl p-8 space-y-6 bg-cover bg-center border-8 border-[#4a2c2a] rounded-lg shadow-2xl"
          style={{
            backgroundImage: "url('/images/wood-panel.png')",
            fontFamily: '"Press Start 2P", cursive',
          }}
        >
          <div className="flex items-center justify-center gap-4 mb-6">
            <GiLaurelsTrophy className="text-5xl text-yellow-300" />
            <h1
              className="text-4xl text-yellow-300 text-center"
              style={{ textShadow: "3px 3px #000" }}
            >
              Ranking da Guilda
            </h1>
          </div>

          {/* Estatísticas Globais */}
          <div className="flex justify-around text-center text-white bg-black/50 p-4 rounded-md">
            <div>
              <p className="text-sm text-gray-400">Total de Simulações</p>
              <p className="text-2xl font-bold">
                {stats?.totalSimulationsRun ?? "..."}
              </p>
            </div>
            <div>
              <p className="text-sm text-gray-400">Taxa de Sucesso Geral</p>
              <p className="text-2xl font-bold">
                {stats
                  ? `${(stats.overallSuccessRate * 100).toFixed(1)}%`
                  : "..."}
              </p>
            </div>
            <div>
              <p className="text-sm text-gray-400">Total de Aventureiros</p>
              <p className="text-2xl font-bold">{stats?.totalUsers ?? "..."}</p>
            </div>
          </div>

          {/* Tabela de Ranking e Controles */}
          {isLoading ? (
            <p className="text-center text-white">Carregando ranking...</p>
          ) : (
            stats && (
              <>
                <RankingTable
                  users={stats.userRankingPage}
                  currentPage={stats.currentPage}
                  pageSize={pageSize}
                />
                <PaginationControls
                  currentPage={stats.currentPage}
                  totalPages={stats.totalPages}
                  onPageChange={setCurrentPage}
                />
              </>
            )
          )}
        </div>
      </main>
    </div>
  );
}
