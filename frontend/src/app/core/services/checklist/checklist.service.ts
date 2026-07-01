// src/app/core/services/checklist/checklist.service.ts

import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChecklistTaskRequest, ChecklistTaskResponse } from '../../models/checklist.models';

@Injectable({
    providedIn: 'root'
})
export class ChecklistService {
    private http = inject(HttpClient);

    // URL base do seu Spring Boot para o checklist
    private apiUrl = 'http://localhost:8080/api/checklist';

    // 1. Criar tarefa (POST)
    createTask(task: ChecklistTaskRequest): Observable<ChecklistTaskResponse> {
        return this.http.post<ChecklistTaskResponse>(`${this.apiUrl}/create`, task);
    }

    // 2. Buscar tarefas do usuário logado (GET)
    getTasksByUser(): Observable<ChecklistTaskResponse[]> {
        return this.http.get<ChecklistTaskResponse[]>(`${this.apiUrl}/getTasksByUser`);
    }

    // 3. Marcar/Desmarcar como concluída (PATCH)
    toggleTask(taskId: number): Observable<any> {
        // Mandamos um corpo vazio {} porque o ID já está na URL
        return this.http.patch(`${this.apiUrl}/${taskId}/toggle`, {});
    }

    // 4. Deletar tarefa (DELETE)
    deleteTask(taskId: number): Observable<any> {
        return this.http.delete(`${this.apiUrl}/delete/${taskId}`);
    }
}