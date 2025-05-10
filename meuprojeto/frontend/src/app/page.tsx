"use client";
import React, { useState, useEffect } from "react";
import api from "@/app/utils/api";
import {
  IterationStatusDTO,
  CreatureResponseDTO,
} from "@/app/utils/types/types";

export default function Home() {
  const [simulationStatus, setSimulationStatus] = useState<IterationStatusDTO>({
    statusCreatures: [],
    inactiveCreatures: [],
    iterationCount: 0,
    isFinished: false,
  });

  const fetchSimulationStatus = async () => {
    try {
      const response = await api.post("/iterar"); // Usando a URL base definida em api.ts
      const data: IterationStatusDTO = response.data;
      setSimulationStatus(data);
    } catch (error) {
      console.error("Erro ao buscar status da simulação:", error);
    }
  };

  const startSimulation = async () => {
    try {
      const response = await api.post("/iniciar", {
        quantidade: 10, // Exemplo de dados que você envia
      });
      if (response.status === 200) {
        console.log("Simulação iniciada!");
      }
    } catch (error) {
      console.error("Erro ao iniciar simulação:", error);
    }
  };

  const resetSimulation = async () => {
    try {
      const response = await api.post("/resetar");
      if (response.status === 200) {
        console.log("Simulação resetada!");
        setSimulationStatus({
          statusCreatures: [],
          inactiveCreatures: [],
          iterationCount: 0,
          isFinished: false,
        });
      }
    } catch (error) {
      console.error("Erro ao resetar simulação:", error);
    }
  };

  useEffect(() => {
    fetchSimulationStatus();
  }, []);

  return (
    <div className="p-4">
      <h1 className="text-2xl font-bold">Simulador de Criaturas</h1>
      <div className="my-4">
        <button
          onClick={startSimulation}
          className="px-4 py-2 bg-blue-500 text-white rounded"
        >
          Iniciar Simulação
        </button>
        <button
          onClick={resetSimulation}
          className="px-4 py-2 bg-red-500 text-white rounded ml-2"
        >
          Resetar Simulação
        </button>
      </div>

      <div className="my-4">
        <h2 className="text-xl">Status da Simulação</h2>
        <p>Iterações: {simulationStatus.iterationCount}</p>
        <p>
          Status: {simulationStatus.isFinished ? "Finalizada" : "Em andamento"}
        </p>
      </div>

      <div className="my-4">
        <h2 className="text-xl">Criaturas Ativas</h2>
        <ul>
          {simulationStatus.statusCreatures.map(
            (creature: CreatureResponseDTO) => (
              <li key={creature.id}>
                Criatura {creature.id}: Posição: {creature.x}, Ouro:{" "}
                {creature.gold}
              </li>
            )
          )}
        </ul>
      </div>

      <div className="my-4">
        <h2 className="text-xl">Criaturas Inativas</h2>
        <ul>
          {simulationStatus.inactiveCreatures.map(
            (creature: CreatureResponseDTO) => (
              <li key={creature.id}>
                Criatura {creature.id}: Posição: {creature.x}, Ouro:{" "}
                {creature.gold}
              </li>
            )
          )}
        </ul>
      </div>
    </div>
  );
}
