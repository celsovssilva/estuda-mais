export interface GoalRequest {
    category: string;
    targetMinutesPerDay: number;
}

export interface GoalResponse {
    id: number;
    category: string;
    targetMinutesPerDay: number;
}

export interface StudySessionRequest {
    subject: string;
    durationMinutes: number;
}

export interface StudySessionResponse {
    id: number;
    subject: string;
    durationMinutes: number;
    studyDate: string; 
    feedbackMessage: string | null;
}