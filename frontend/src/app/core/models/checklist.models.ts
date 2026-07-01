export interface ChecklistTaskRequest {
    description: string;
    executionDate: string;
}

export interface ChecklistTaskResponse {
    id: number;
    description: string;
    completed: boolean;
    executionDate: string;
}