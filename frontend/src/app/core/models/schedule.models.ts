export interface ScheduleRequest {
    title: string;
    description: string;
    targetDate: string;
    type: 'DAY' | 'WEEK' | 'MONTH' | 'YEAR' | string;
    startTime: string | null;
    endTime: string | null;
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
}