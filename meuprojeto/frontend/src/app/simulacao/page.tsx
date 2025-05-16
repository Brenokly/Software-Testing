"use client";

import { CenarioCorrida } from "@/components/CenarioCorrida";
import {
  getCriaturaAtual,
  iniciarSimulacao,
  iterar,
  resetar,
} from "@/utils/services/simulacaoService";
import { IterationStatusDTO } from "@/utils/types/types";
import { useSearchParams } from "next/navigation";
import { useEffect, useState } from "react";

export default function SimulacaoPage() {
  const [status, setStatus] = useState<IterationStatusDTO | null>(null);
  const [criaturaAtualId, setCriaturaAtualId] = useState<number | null>(null);
  const [quantidadeCriaturas, setQuantidade] = useState(10);
  const searchParams = useSearchParams();

  useEffect(() => {
    const qtd = Number(searchParams.get("quantidade"));
    if (!isNaN(qtd)) setQuantidade(qtd);
  }, [searchParams]);

  useEffect(() => {
    async function iniciar() {
      try {
        const dados = await iniciarSimulacao(quantidadeCriaturas);
        setStatus(dados);
        const atual = await getCriaturaAtual();
        setCriaturaAtualId(atual);
      } catch (error) {
        console.error("Erro ao iniciar simulação:", error);
      }
    }
    iniciar();
  }, [quantidadeCriaturas]);

  async function handleIterar() {
    const dados = await iterar();
    setStatus(dados);
    const atual = await getCriaturaAtual();
    setCriaturaAtualId(atual);
  }

  async function handleResetar() {
    const dados = await resetar();
    setStatus(dados);
    const atual = await getCriaturaAtual();
    setCriaturaAtualId(atual);
  }

  return (
    <div
      className="w-screen h-screen"
      style={{
        backgroundImage: "url('/grass.png')",
        backgroundRepeat: "repeat",
        backgroundSize: "auto",
        imageRendering: "pixelated",
      }}
    >
      <div className="w-full h-[700px] relative flex border-2 border-black rounded">
        <div className="w-1/2">
          <CenarioCorrida
            criaturas={status?.statusCreatures || []}
            criaturaAtualId={criaturaAtualId}
          />
        </div>
        <div
          className="w-1/2 bg-amber-50 m-5 flex flex-col gap-4 p-4 border-2 border-black rounded overflow-auto"
          style={{
            backgroundImage: "url('/terra.png')",
            backgroundRepeat: "repeat",
            backgroundSize: "auto",
            imageRendering: "pixelated",
          }}
        >
          {/* Iteration Info */}
          <div className="bg-white/80 p-3 rounded shadow font-mono">
            <p className="text-xl font-bold">
              - Iteração #{status?.iterationCount ?? "—"}
            </p>
            <p className="text-sm">
              {status?.finished
                ? "✅ Simulação Finalizada"
                : "🔄 Simulação em Andamento"}
            </p>
          </div>

          {/* Criaturas Ativas */}
          <div className="bg-green-100 p-3 rounded shadow font-mono flex flex-col min-h-0">
            <p className="text-lg font-bold mb-2">🟢 Criaturas Ativas</p>
            <ul className="space-y-1 overflow-y-auto pr-2 flex-1">
              {status?.statusCreatures.map((c) => (
                <li key={c.id} className="text-sm">
                  🧍‍♂️ ID: {c.id} | X: {c.x} | 🪙 Ouro: {c.gold}
                </li>
              ))}
              {status?.statusCreatures.length === 0 && (
                <p>Nenhuma criatura ativa.</p>
              )}
            </ul>
          </div>

          {/* Criaturas Inativas */}
          <div className="bg-red-100 p-3 rounded shadow font-mono">
            <p className="text-lg font-bold mb-2">🔴 Criaturas Inativas</p>
            <ul className="space-y-1 overflow-y-auto pr-2 flex-1">
              {status?.inactiveCreatures.map((c) => (
                <li key={c.id} className="text-sm">
                  🧍‍♂️ ID: {c.id} | X: {c.x} | 🪙 Ouro: {c.gold}
                </li>
              ))}
              {status?.inactiveCreatures.length === 0 && (
                <p>Nenhuma criatura inativa.</p>
              )}
            </ul>
          </div>
        </div>
      </div>

      {/* Botões */}
      <div className="absolute bottom-4 left-1/2 -translate-x-1/2 z-50 flex gap-4 bg-white/80 px-4 py-2 rounded shadow">
        <button
          onClick={handleIterar}
          className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded"
        >
          Iterar
        </button>
        <button
          onClick={handleResetar}
          className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded"
        >
          Resetar
        </button>
      </div>
    </div>
  );
}
