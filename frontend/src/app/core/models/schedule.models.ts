export interface ScheduleRequest {
    title: string;
    description: string;
    targetDate: string;
    type: 'DAY' | 'WEEK' | 'MONTH' | 'YEAR' | string;
}

export interface ScheduleResponse {
    id: number;
    title: string;
    description: string;
    targetDate: string;
    type: string;
}