export interface HorizonEntityDTO {
    id: number;
    x: number;
    gold: number;
    type: 'CREATURE_UNIT' | 'CREATURE_CLUSTER';
}

export interface GuardianDTO {
    id: number;
    x: number;
    gold: number;
}

export enum SimulationStatus {
    RUNNING = 'RUNNING',
    SUCCESSFUL = 'SUCCESSFUL',
    FAILED = 'FAILED',
}

export interface HorizonDTO {
    entities: HorizonEntityDTO[];
    guardiao: GuardianDTO;
    status: SimulationStatus;
}