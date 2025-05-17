"use client";
import { CreatureResponseDTO } from "@/utils/types/types";
import { useEffect } from "react";
import { Creature } from "./Creature";

interface CenarioCorridaProps {
  criaturas: CreatureResponseDTO[];
  criaturaAtualId: number | null;
  onFinishDetected?: () => void;
  alturaPista?: number;
  espacamento?: number;
}

export function CenarioCorrida({
  criaturas,
  criaturaAtualId,
  onFinishDetected,
  alturaPista = 40,
  espacamento = 27,
}: CenarioCorridaProps) {
  useEffect(() => {
    const algumaChegouAoFinal = criaturas.some(
      (criatura) => criatura.x >= 10_000_000
    );
    if (algumaChegouAoFinal) {
      // Avisa o pai que acabou a corrida
      if (onFinishDetected) {
        onFinishDetected();
      }
    }
  }, [criaturas, onFinishDetected]);

  return (
    <div
      className="w-full h-full"
      style={{
        display: "flex",
        flexDirection: "column",
        gap: `${espacamento}px`,
      }}
    >
      {criaturas.map((criatura, index) => (
        <div
          key={`pista-${criatura.id}`}
          style={{
            width: "100%",
            height: `${alturaPista}px`,
            backgroundImage: "url('/terra.png')",
            backgroundRepeat: "repeat",
            backgroundSize: "auto",
            imageRendering: "pixelated",
            display: "flex",
            alignItems: "start",
            position: "relative",
            zIndex: 1,
            marginTop: index === 0 ? `${27}px` : undefined,
          }}
        >
          <div style={{ position: "relative", zIndex: 10 }}>
            <Creature
              Creature={criatura}
              isSelected={criatura.id === criaturaAtualId}
            />
          </div>
        </div>
      ))}
    </div>
  );
}
