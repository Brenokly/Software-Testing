"use client";

import { HorizonDTO, SimulationStatus } from "@/utils/types/simulationTypes";

interface InfoPanelProps {
  horizon: HorizonDTO | null;
  iterationCount: number;
}

export const InfoPanel = ({ horizon, iterationCount }: InfoPanelProps) => {
  const statusText = {
    [SimulationStatus.RUNNING]: "Batalha em Andamento...",
    [SimulationStatus.SUCCESSFUL]: "Vitória Conquistada!",
    [SimulationStatus.FAILED]: "Derrota no Horizonte...",
  };

  return (
    <div
      className="w-full h-full bg-amber-50 flex flex-col gap-4 p-4 border-2 border-black rounded overflow-auto"
      style={{
        backgroundImage: "url('/images/terra.png')",
        imageRendering: "pixelated",
      }}
    >
      {/* Bloco de Iteration Info */}
      <div className="bg-white/80 p-3 rounded shadow font-mono">
        <p className="text-lg font-bold">Iteração #{iterationCount ?? "—"}</p>
        <p className="text-sm">
          {horizon ? statusText[horizon.status] : "Aguardando..."}
        </p>
      </div>

      {/* Painel do Guardião com Posição X */}
      <div className="bg-red-100 p-3 rounded shadow font-mono">
        <p className="text-lg font-bold mb-2">🛡️ Guardião</p>
        {horizon && (
          <p className="text-sm">
            ID: {horizon.guardiao.id} |{/* CORREÇÃO: Adicionado o X */}
            X: {horizon.guardiao.x.toFixed(0)} | 💰 Ouro:{" "}
            {horizon.guardiao.gold.toFixed(0)}
          </p>
        )}
      </div>

      {/* Painel das Criaturas Ativas com Posição X */}
      <div className="bg-green-100 p-3 rounded shadow font-mono flex flex-col min-h-0 flex-1">
        <p className="text-lg font-bold mb-2">🟢 Entidades Ativas</p>
        <ul className="space-y-1 overflow-y-auto pr-2 flex-1">
          {horizon?.entities.map((c) => (
            <li key={c.id} className="text-sm">
              {c.type === "CREATURE_CLUSTER" ? "뭉" : "🧍"} ID: {c.id} |
              {/* CORREÇÃO: Adicionado o X */}
              X: {c.x.toFixed(0)} | 💰 Ouro: {c.gold.toFixed(0)}
            </li>
          ))}
          {(!horizon || horizon.entities.length === 0) && (
            <p>Nenhuma criatura ativa.</p>
          )}
        </ul>
      </div>
    </div>
  );
};
