// types.ts

export interface CreatureResponseDTO {  
  id: number; // ID da criatura
  x: number; // Posição da criatura no eixo X (Horizonte)
  gold: number; // Quantidade de ouro da criatura
}

export interface IterationStatusDTO {
  statusCreatures: CreatureResponseDTO[]; // Criaturas ativas
  inactiveCreatures: CreatureResponseDTO[]; // Criaturas inativas
  iterationCount: number; // Contador de iterações
  isFinished: boolean; // Indica se a simulação terminou
}
