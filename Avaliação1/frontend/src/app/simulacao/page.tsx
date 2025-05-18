"use client";

import { CenarioCorrida } from "@/components/CenarioCorrida";
import {
  finalizar,
  getCriaturaAtual,
  iniciarSimulacao,
  iterar,
  resetar,
} from "@/utils/services/simulacaoService";
import { IterationStatusDTO } from "@/utils/types/types";
import { useRouter, useSearchParams } from "next/navigation";
import { useCallback, useEffect, useMemo, useState } from "react";

export default function SimulacaoPage() {
  const [status, setStatus] = useState<IterationStatusDTO | null>(null);
  const [criaturaAtualId, setCriaturaAtualId] = useState<number | null>(null);
  const [winner, setWinner] = useState<number | null>(null);
  const [autoIterar, setAutoIterar] = useState(false);

  // Estado para velocidade da iteração (intervalo em ms)
  // Começa em 500 ms (velocidade normal)
  const [intervaloIteracao, setIntervaloIteracao] = useState(500);

  const searchParams = useSearchParams();
  const router = useRouter();

  const quantidadeCriaturas = useMemo(() => {
    const qtd = Number(searchParams.get("quantidade"));
    return isNaN(qtd) ? 10 : qtd;
  }, [searchParams]);

  useEffect(() => {
    if (quantidadeCriaturas <= 1 || quantidadeCriaturas > 10) {
      router.push("/"); // Redireciona para a home
    }
  }, [quantidadeCriaturas, router]);

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

  const atualizarStatus = useCallback(
    async (acao: () => Promise<IterationStatusDTO>) => {
      const dados = await acao();
      setStatus(dados);
      const atual = await getCriaturaAtual();
      setCriaturaAtualId(atual);

      return dados;
    },
    []
  );

  const handleIterar = useCallback(() => {
    if (status?.finished) return;
    return atualizarStatus(iterar);
  }, [status, atualizarStatus]);

  const toggleAutoIterar = () => setAutoIterar((v) => !v);

  // Efeito para iteração automática com velocidade variável
  useEffect(() => {
    if (!autoIterar || status?.finished) {
      setAutoIterar(false);
      return;
    }

    const intervalId = setInterval(() => {
      handleIterar();
    }, intervaloIteracao);

    return () => clearInterval(intervalId);
  }, [autoIterar, handleIterar, intervaloIteracao, status]);

  const handleResetar = useCallback(() => {
    // se estiver iterando, para a iteração automática
    if (autoIterar) {
      setAutoIterar(false);
    }
    return atualizarStatus(resetar);
  }, [atualizarStatus, autoIterar]);

  const handleFinalizar = useCallback(async (): Promise<void> => {
    if (status?.finished) return;
    await atualizarStatus(finalizar);
  }, [status, atualizarStatus]);

  const onFinishDetected = useCallback(() => {
    if (!status?.finished) {
      handleFinalizar();
    }
  }, [status, handleFinalizar]);

  useEffect(() => {
    if (status?.finished) {
      const vencedores = status.statusCreatures.filter(
        (c) => c.x >= 10_000_000
      );
      if (vencedores.length > 0) {
        setWinner(vencedores[0].id);
      }
    }
  }, [status?.finished, status?.statusCreatures]);

  // Função para aumentar a velocidade da iteração em até 4x
  // Velocidade padrão = 500ms
  // 1x -> 500ms, 2x -> 250ms, 3x -> 167ms, 4x -> 125ms
  const aumentarVelocidade = () => {
    setIntervaloIteracao((atual) => {
      if (atual === 500) return 250;
      if (atual === 250) return 167;
      if (atual === 167) return 125;
      return 500; // volta para velocidade normal após 4x
    });
  };

  return (
    <div
      className="w-screen h-screen relative"
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
            onFinishDetected={onFinishDetected} // callback para aviso de fim
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
            <p className="text-x font-bold">
              - Iteração #{status?.iterationCount ?? "—"}
            </p>
            <p className="text-sm">
              {status?.finished
                ? "✅ Simulação Finalizada"
                : "🔄 Simulação em Andamento"}
            </p>

            {winner !== null && (
              <p className="mt-2 text-black font-bold">
                🏆 Vencedor: Criatura #{winner}
              </p>
            )}
          </div>

          {/* Criaturas Ativas */}
          <div className="bg-green-100 p-3 rounded shadow font-mono flex flex-col min-h-0">
            <p className="text-lg font-bold mb-2">🟢 Criaturas Ativas</p>
            <ul className="space-y-1 overflow-y-auto pr-2 flex-1">
              {status?.statusCreatures.map((c) => (
                <li key={c.id} className="text-sm">
                  🧍‍♂️ ID: {c.id} | X: {c.x.toFixed(2)} | 🪙 Ouro:{" "}
                  {c.gold.toFixed(2)}
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
                  🧍‍♂️ ID: {c.id} | X: {c.x.toFixed(2)} | 🪙 Ouro:{" "}
                  {c.gold.toFixed(2)}
                </li>
              ))}
              {status?.inactiveCreatures.length === 0 && (
                <p>Nenhuma criatura inativa.</p>
              )}
            </ul>
          </div>
        </div>
      </div>

      <div className="absolute bottom-10 left-1/2 flex items-center justify-center gap-10 bg-white/80 px-4 py-2 rounded shadow -translate-x-1/2 z-50">
        <button
          onClick={handleIterar}
          className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded"
        >
          Iterar uma vez
        </button>
        <button
          onClick={handleResetar}
          className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded"
        >
          Resetar Iteração
        </button>
        <button
          onClick={toggleAutoIterar}
          className={`px-4 py-2 rounded text-white ${
            autoIterar
              ? "bg-green-600 hover:bg-green-700"
              : "bg-gray-600 hover:bg-gray-700"
          }`}
        >
          {autoIterar ? "Parar iteração automática" : "iteração automática"}
        </button>
        {/* Botão novo para aumentar velocidade */}
        <button
          onClick={aumentarVelocidade}
          className="bg-purple-600 hover:bg-purple-700 text-white px-4 py-2 rounded"
        >
          Velocidade: {Math.round(500 / intervaloIteracao)}x
        </button>
      </div>
    </div>
  );
}
