import { CreatureResponseDTO } from "@/utils/types/types";
import Image from "next/image";
import { useEffect, useRef, useState } from "react";

// Mapeamento de filtros baseado no ID da criatura
const filterMap: Record<number, string> = {
  1: "hue-rotate(0deg)",
  2: "hue-rotate(60deg)",
  3: "hue-rotate(120deg)",
  4: "hue-rotate(180deg)",
  5: "hue-rotate(240deg)",
  6: "hue-rotate(300deg)",
  7: "sepia(100%) saturate(500%)",
  8: "brightness(1.5)",
  9: "contrast(1.5)",
  10: "grayscale(100%)",
};

interface CriaturaProps {
  Creature: CreatureResponseDTO;
  isSelected?: boolean;
}

export function Creature({ Creature, isSelected = false }: CriaturaProps) {
  const [frame, setFrame] = useState(0);
  const totalFrames = 7;
  const frameWidth = 32;
  const frameHeight = 32;

  const previousXRef = useRef(Creature.x);
  const [indoParaFrente, setIndoParaFrente] = useState(true);

  useEffect(() => {
    const previousX = previousXRef.current;
    setIndoParaFrente(Creature.x >= previousX);
    previousXRef.current = Creature.x;
  }, [Creature.x]);

  useEffect(() => {
    const interval = setInterval(() => {
      setFrame((f) => (f + 1) % totalFrames);
    }, 100);
    return () => clearInterval(interval);
  }, []);

  const criaturaFilter = filterMap[Creature.id] || "none"; // Filtro padrão caso o id não esteja mapeado

  return (
    <div
      className="relative flex flex-col items-center transition-all duration-500"
      style={{
        transform: `translateX(${Creature.x * 2}px)`,
      }}
    >
      {isSelected && (
        <div className="absolute top-[-20px] animate-bounce text-red-600 text-lg font-bold">
          <Image
            src="/seta.png"
            alt="Selecionado"
            width={512}
            height={512}
            className="w-4 h-4"
          />
        </div>
      )}

      <div
        className="bg-no-repeat"
        style={{
          backgroundImage: `url(/creature.png)`,
          backgroundPosition: `-${frame * frameWidth}px 0px`,
          width: `${frameWidth}px`,
          height: `${frameHeight}px`,
          transform: `${
            indoParaFrente ? "scaleX(1)" : "scaleX(-1)"
          } scale(3.5)`,
          imageRendering: "pixelated",
          filter: criaturaFilter,
          marginLeft: 8,
        }}
      />
    </div>
  );
}
