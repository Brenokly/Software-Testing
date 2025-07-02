"use client";

import {
  runNextIterationService,
  startNewSimulationService,
} from "@/utils/services/simulationService";
import { HorizonDTO, SimulationStatus } from "@/utils/types/simulationTypes";
import { useCallback, useEffect, useState } from "react";
import { Controls } from "./Controls";
import { HorizonView } from "./HorizonView";
import { InfoPanel } from "./InfoPanel";

interface SimulationClientProps {
  initialCreatureCount: number;
  onReturnToSetup: () => void;
}

export const SimulationClient = ({
  initialCreatureCount,
  onReturnToSetup,
}: SimulationClientProps) => {
  const [horizon, setHorizon] = useState<HorizonDTO | null>(null);
  const [iterationCount, setIterationCount] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isAutoRunning, setIsAutoRunning] = useState(false);

  const handleReset = () => {
    onReturnToSetup();
  };

  const handleStart = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    setIsAutoRunning(false);
    try {
      const initialHorizon = await startNewSimulationService(
        initialCreatureCount
      );
      setHorizon(initialHorizon);
      setIterationCount(0);
    } catch (err) {
      setError("Falha ao iniciar a simulação.");
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  }, [initialCreatureCount]);

  useEffect(() => {
    handleStart();
  }, [handleStart]);

  const handleIterate = useCallback(async () => {
    if (!horizon || horizon.status !== SimulationStatus.RUNNING) {
      setIsAutoRunning(false);
      return;
    }
    try {
      const nextHorizon = await runNextIterationService(horizon);
      setHorizon(nextHorizon);
      setIterationCount((prev) => prev + 1);
    } catch (err) {
      setError("Falha ao executar a iteração.");
      console.error(err);
    }
  }, [horizon]);

  useEffect(() => {
    if (!isAutoRunning || horizon?.status !== SimulationStatus.RUNNING) return;
    const intervalId = setInterval(() => handleIterate(), 500);
    return () => clearInterval(intervalId);
  }, [isAutoRunning, handleIterate, horizon?.status]);

  if (isLoading)
    return (
      <div className="w-full h-full flex items-center justify-center bg-gray-900 text-white">
        <p>Carregando...</p>
      </div>
    );
  if (error)
    return (
      <div className="w-full h-full flex items-center justify-center bg-red-900 text-white">
        <p>Erro: {error}</p>
      </div>
    );

  return (
    <div
      // MUDANÇA: Usando flex-1 para ocupar o espaço disponível e overflow-hidden
      className="flex-1 relative flex overflow-hidden"
      // CORREÇÃO DA GRAMA: Usando bg-repeat para texturizar sem esticar.
      style={{
        backgroundImage: "url('/images/grass.png')",
        backgroundRepeat: "repeat",
        imageRendering: "pixelated",
      }}
    >
      <div className="w-2/3 h-full mt-05">
        <HorizonView horizon={horizon} />
      </div>
      <div className="w-1/3 h-full">
        <InfoPanel horizon={horizon} iterationCount={iterationCount} />
      </div>

      <Controls
        onIterate={handleIterate}
        onReset={handleReset}
        onToggleAuto={() => setIsAutoRunning((prev) => !prev)}
        isAutoRunning={isAutoRunning}
        isFinished={horizon?.status !== SimulationStatus.RUNNING}
      />
    </div>
  );
};
