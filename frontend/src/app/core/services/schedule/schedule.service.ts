import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ScheduleRequest, ScheduleResponse } from '../../models/schedule.models';

@Injectable({
    providedIn: 'root'
})
export class ScheduleService {
    private http = inject(HttpClient);
    private apiUrl = 'http://localhost:8080/api/schedule'; // Ajuste se a porta for diferente

    createSchedule(schedule: ScheduleRequest): Observable<ScheduleResponse> {
        return this.http.post<ScheduleResponse>(`${this.apiUrl}/create`, schedule);
    }

    getSchedulesByUser(): Observable<ScheduleResponse[]> {
        return this.http.get<ScheduleResponse[]>(`${this.apiUrl}/getByUser`);
    }

    getSchedulesByType(type: string): Observable<ScheduleResponse[]> {
        return this.http.get<ScheduleResponse[]>(`${this.apiUrl}/getByTypeSchedule?type=${type}`);
    }

    updateSchedule(id: number, schedule: ScheduleRequest): Observable<ScheduleResponse> {
        return this.http.put<ScheduleResponse>(`${this.apiUrl}/update/${id}`, schedule);
    }

    deleteSchedule(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/delete/${id}`);
    }
}