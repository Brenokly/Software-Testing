"use client";
import { useRouter } from "next/navigation";
import { useState } from "react";

export default function Home() {
  const [numCriaturas, setNumCriaturas] = useState("10"); // string para controlar input texto
  const [erro, setErro] = useState("");
  const router = useRouter();

  const validar = (valor: number) => {
    if (valor <= 1 || valor > 10) {
      setErro("Quantidade deve ser entre 2 e 10.");
      return false;
    }
    setErro("");
    return true;
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const valorTexto = e.target.value;
    if (/^\d*$/.test(valorTexto)) {
      setNumCriaturas(valorTexto);
      if (valorTexto === "") {
        setErro("Quantidade deve ser entre 2 e 10.");
        return;
      }
      validar(Number(valorTexto));
    }
  };

  const iniciarSimulacao = () => {
    const valorNum = Number(numCriaturas);
    if (!validar(valorNum)) return;
    router.push(`/simulacao/${valorNum}`);
  };

  return (
    <div
      className="min-h-screen w-full flex items-end justify-center"
      style={{
        backgroundImage: "url('/ceu.png')",
        backgroundRepeat: "repeat-x",
        backgroundSize: "cover",
        imageRendering: "pixelated",
        position: "relative",
      }}
    >
      {/* Chão com grama */}
      <div
        style={{
          backgroundImage: "url('/grass.png')",
          backgroundRepeat: "repeat-x",
          backgroundSize: "contain",
          height: "100px",
          width: "100%",
          position: "absolute",
          bottom: 0,
          imageRendering: "pixelated",
        }}
      ></div>

      {/* Painel de controle */}
      <div
        className="z-10 p-8 w-[90%] max-w-md flex flex-col items-center justify-between"
        style={{
          backgroundImage: "url('/painel-madeira.jpg')",
          backgroundRepeat: "no-repeat",
          backgroundSize: "cover",
          imageRendering: "pixelated",
          border: "4px solid #333",
          borderRadius: "12px",
          boxShadow: "4px 4px 0px #000",
          minHeight: "260px",
          maxWidth: "400px",
          marginBottom: "40px",
        }}
      >
        <div>
          <h1
            className="text-3xl mb-8 text-center"
            style={{
              fontFamily: "'Press Start 2P', cursive",
              color: "#fff",
              textShadow: "2px 2px #000",
            }}
          >
            Simulador de Criaturas
          </h1>

          <label
            htmlFor="numCriaturas"
            className="block mb-4 text-sm font-bold text-center"
            style={{ letterSpacing: "0.05em" }}
          >
            Quantas criaturas você quer simular?
          </label>

          <input
            id="numCriaturas"
            type="text"
            value={numCriaturas}
            onChange={handleChange}
            className="w-full mx-auto px-6 py-4 rounded border-2 border-gray-600 bg-black text-white text-center"
            style={{
              fontFamily: "'Press Start 2P', cursive",
              fontSize: "16px",
              marginBottom: "15px",
            }}
            inputMode="numeric"
            pattern="[0-9]*"
            maxLength={2}
          />

          {erro && (
            <p
              className="text-black text-x font-bold mb-4 min-h-[20px]"
              style={{ width: "100%", textAlign: "center" }}
            >
              {erro}
            </p>
          )}
        </div>

        {/* Botão alinhado à direita no final do painel */}
        <div className="w-full flex justify-center">
          <button
            onClick={iniciarSimulacao}
            disabled={!!erro}
            className="py-3 px-8 rounded-2xl border-4 border-black font-bold cursor-pointer bg-gradient-to-r from-[#ff7e5f] to-[#feb47b] hover:from-[#feb47b] hover:to-[#ff7e5f] transition duration-300"
            style={{
              fontFamily: "'Press Start 2P', cursive",
              fontSize: "14px",
              boxShadow: "2px 2px 0px #000",
              userSelect: "none",
            }}
          >
            Iniciar Simulação
          </button>
        </div>
      </div>
    </div>
  );
}
