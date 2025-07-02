"use client";

import { GuardianDTO, HorizonEntityDTO } from "@/utils/types/simulationTypes";
import { useEffect, useRef, useState } from "react";

interface EntitySpriteProps {
  entity: HorizonEntityDTO | GuardianDTO;
  isGuardian?: boolean;
  verticalOffset: number; // Para "espalhar" as criaturas na pista
}

const SPRITE_CONFIG = {
  creature: {
    url: "/images/Creature.png",
    frames: 7,
    width: 32,
    height: 32,
    scale: 2,
  },
  cluster: {
    url: "/images/Creature.png",
    frames: 7,
    width: 32,
    height: 32,
    scale: 3.0,
  },
  guardian: {
    url: "/images/Guardian.png",
    frames: 4,
    width: 100,
    height: 100,
    scale: 2.5,
  },
};

export const EntitySprite = ({
  entity,
  isGuardian = false,
  verticalOffset,
}: EntitySpriteProps) => {
  const [frame, setFrame] = useState(0);
  const previousXRef = useRef(entity.x);
  const [isMovingForward, setIsMovingForward] = useState(true);

  let entityType: "creature" | "cluster" | "guardian" = "creature";
  if (isGuardian) {
    entityType = "guardian";
  } else if ((entity as HorizonEntityDTO).type === "CREATURE_CLUSTER") {
    entityType = "cluster";
  }

  const config = SPRITE_CONFIG[entityType];

  useEffect(() => {
    const interval = setInterval(
      () => setFrame((f) => (f + 1) % config.frames),
      150
    );
    return () => clearInterval(interval);
  }, [config.frames]);

  useEffect(() => {
    if (entity.x !== previousXRef.current) {
      setIsMovingForward(entity.x > previousXRef.current);
      previousXRef.current = entity.x;
    }
  }, [entity.x]);

  const maxPosition = 10_000_000;
  const leftPercentage = (entity.x / maxPosition) * 100;

  return (
    <div
      className="absolute transition-all duration-500 ease-linear"
      style={{
        left: `clamp(0%, ${leftPercentage}%, calc(100% - ${
          config.width * config.scale
        }px))`,
        // ATUALIZAÇÃO: Usa o offset vertical para espalhar as criaturas
        top: `${verticalOffset}px`,
        zIndex: verticalOffset, // Criaturas "mais abaixo" aparecem na frente
      }}
    >
      <div
        className="bg-no-repeat bg-center"
        style={{
          backgroundImage: `url(${config.url})`,
          backgroundPosition: `-${frame * config.width}px 0px`,
          width: `${config.width}px`,
          height: `${config.height}px`,
          transform: `scaleX(${isMovingForward ? 1 : -1}) scale(${
            config.scale
          })`,
          imageRendering: "pixelated",
        }}
      />
      <div className="absolute -top-5 left-1/2 -translate-x-1/2 bg-black bg-opacity-70 text-white text-xs rounded px-1 py-0.5">
        ID:{entity.id}
      </div>
    </div>
  );
};
