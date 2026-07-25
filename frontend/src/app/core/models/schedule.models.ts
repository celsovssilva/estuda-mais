export interface ScheduleRequest {
    title: string;
    description: string;
    targetDate: string;
    type: 'DAY' | 'WEEK' | 'MONTH' | 'YEAR' | string;
    startTime: string | null;
    endTime: string | null;
    category: 'ACADEMIA' | 'ESTUDOS' | 'CUIDADO_PESSOAL' | 'OUTROS' | 'SIMULADO'| 'AULAS'| 'LEITURA'|
    'REDACAO'| string;
}

export interface ScheduleResponse {
    id: number;
    title: string;
    description: string;
    targetDate: string;
    type: string;
    startTime: string | null;
    endTime: string | null;
    completed: boolean;
    category: string;
}

export interface CategoryMetric {
    category: string;
    total: number;
    completed: number;
    percentage: number;
}