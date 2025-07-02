"use client";

import { SimulationClient } from "@/components/simulation/SimulationClient";
import { useState } from "react";

export default function SimulacaoPage() {
  const [creatureCount, setCreatureCount] = useState<number | null>(null);
  const [countInput, setCountInput] = useState<string>("10");
  const [inputError, setInputError] = useState<string>("");

  const handleStart = () => {
    const count = parseInt(countInput, 10);
    if (count > 0 && count <= 10) {
      setInputError("");
      setCreatureCount(count);
    } else {
      setInputError("Por favor, insira um número entre 1 e 10.");
    }
  };

  const handleReturnToSetup = () => {
    setCreatureCount(null);
  };

  return (
    <div>
      <main className="flex flex-col min-h-screen pt-[80px]">
        {creatureCount === null ? (
          <>
            <div
              className="fixed inset-0 w-full h-full bg-cover bg-center -z-10"
              style={{ backgroundImage: "url('/images/fildAventure.jpg')" }}
            ></div>

            <div className="w-full h-full flex flex-col items-center justify-center p-24">
              <div className="z-10 text-center bg-white/80 p-10 rounded-lg shadow-2xl border-4 border-black">
                <h1
                  className="text-3xl font-bold mb-4"
                  style={{ fontFamily: '"Press Start 2P", cursive' }}
                >
                  Prepare a Simulação
                </h1>
                <p className="mb-4">Quantas criaturas você quer simular?</p>
                <input
                  type="number"
                  value={countInput}
                  onChange={(e) => setCountInput(e.target.value)}
                  className="text-center p-2 border-2 border-black rounded-md mb-2 w-48"
                  min="1"
                  max="10"
                />
                {inputError && (
                  <p className="text-red-600 text-sm mb-2">{inputError}</p>
                )}
                <button
                  onClick={handleStart}
                  className="block mx-auto px-6 py-3 bg-green-500 text-white font-bold rounded-md border-2 border-black hover:bg-green-600"
                >
                  Iniciar Batalha!
                </button>
              </div>
            </div>
          </>
        ) : (
          <SimulationClient
            initialCreatureCount={creatureCount}
            onReturnToSetup={handleReturnToSetup}
          />
        )}
      </main>
    </div>
  );
}
