import { CreatureResponseDTO } from "@/utils/types/types";
import { Creature } from "./Creature";

interface CenarioCorridaProps {
  criaturas: CreatureResponseDTO[];
  criaturaAtualId: number | null;
  alturaPista?: number;
  espacamento?: number;
}

export function CenarioCorrida({
  criaturas,
  criaturaAtualId,
  alturaPista = 40,
  espacamento = 27,
}: CenarioCorridaProps) {
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
