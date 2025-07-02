"use client";

import { HorizonDTO } from "@/utils/types/simulationTypes";
import { EntitySprite } from "./EntitySprite";

interface HorizonViewProps {
  horizon: HorizonDTO | null;
}

// Um array com 10 posições fixas para as criaturas que começa em 20 e vai incrementando de 17
const positions = Array.from({ length: 10 }, (_, i) => 20 + i * 70);

export const HorizonView = ({ horizon }: HorizonViewProps) => {
  if (!horizon) return null;

  return (
    <div
      className="w-full h-full p-[348px] bg-repeat relative overflow-hidden border-b-8 border-yellow-900/50"
      style={{
        backgroundImage: "url('/images/terra.png')",
        imageRendering: "pixelated",
      }}
    >
      {/* Renderiza todas as criaturas e clusters na mesma arena */}
      {horizon.entities.map((entity, index) => (
        <EntitySprite
          key={entity.id}
          entity={entity}
          verticalOffset={positions[index]}
        />
      ))}

      <EntitySprite
        key={horizon.guardiao.id}
        entity={horizon.guardiao}
        isGuardian={true}
        verticalOffset={250}
      />
    </div>
  );
};
