"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

export default function Home() {
  const [numCriaturas, setNumCriaturas] = useState(10);
  const router = useRouter();

  const iniciarSimulacao = () => {
    router.push(`/simulacao?quantidade=${numCriaturas}`);
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-b from-blue-100 to-blue-300">
      <div className="bg-white rounded-2xl shadow-lg p-8 w-[90%] max-w-md text-center">
        <h1 className="text-2xl font-bold text-gray-700 mb-6">
          Simulador de Criaturas
        </h1>
        <label className="block text-gray-600 mb-2">
          Digite o número de criaturas:
        </label>
        <input
          type="number"
          min={1}
          value={numCriaturas}
          onChange={(e) => setNumCriaturas(Number(e.target.value))}
          className="w-full px-4 py-2 rounded border border-gray-300 mb-4 focus:outline-none focus:ring-2 focus:ring-blue-400"
        />
        <button
          onClick={iniciarSimulacao}
          className="bg-green-500 hover:bg-green-600 text-white font-bold py-2 px-4 rounded-full transition"
        >
          Iniciar Simulação
        </button>
      </div>
    </div>
  );
}
