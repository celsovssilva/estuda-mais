import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GoalRequest, GoalResponse, StudySessionRequest, StudySessionResponse } from '../../models/study.models';
import {environment} from "../../../../environment";

@Injectable({
    providedIn: 'root'
})
export class StudyService {
    private http = inject(HttpClient);
    private apiUrl = `${environment.apiUrl}/api/study`;

    // --- Rotas de Metas (Goals) ---
    saveGoal(goal: GoalRequest): Observable<GoalResponse> {
        return this.http.post<GoalResponse>(`${this.apiUrl}/goal/save`, goal);
    }

    getGoals(): Observable<GoalResponse[]> {
        return this.http.get<GoalResponse[]>(`${this.apiUrl}/goal`);
    }

    // --- Rotas de Sessões de Estudo ---
    registerSession(session: StudySessionRequest): Observable<StudySessionResponse> {
        return this.http.post<StudySessionResponse>(`${this.apiUrl}/session/register`, session);
    }

    getSessionHistory(): Observable<StudySessionResponse[]> {
        return this.http.get<StudySessionResponse[]>(`${this.apiUrl}/session/history`);
    }

    // --- Rota do Dashboard de Métricas Integradas ---
    getStudyDashboard(): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}/dashboard`);
    }
}