export interface UserStatisticsDTO {
    login: string;
    score: number;
    simulationsRun: number;
    successRate: number;
}

export interface GlobalStatisticsDTO {
    totalSimulationsRun: number;
    overallSuccessRate: number;
    userStatistics: UserStatisticsDTO[];
}