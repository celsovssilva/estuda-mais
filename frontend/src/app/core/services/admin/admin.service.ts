import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminUser, AdminUpdateUserRequest } from '../../models/admin.models';
import { ScheduleResponse } from '../../models/schedule.models';
import { environment } from "../../../../environments/environment.prod";

@Injectable({
    providedIn: 'root'
})
export class AdminService {
    private http = inject(HttpClient);
    private apiUrl = `${environment.apiUrl}/api/admin`;

    getAllUsers(): Observable<AdminUser[]> {
        return this.http.get<AdminUser[]>(`${this.apiUrl}/getAll`);
    }

    updateUser(userId: number, data: AdminUpdateUserRequest): Observable<AdminUser> {
        return this.http.put<AdminUser>(`${this.apiUrl}/users/${userId}`, data);
    }

    toggleUser(userId: number): Observable<AdminUser> {
        return this.http.patch<AdminUser>(`${this.apiUrl}/users/${userId}/toggle`, {});
    }

    getUserSchedules(userId: number): Observable<ScheduleResponse[]> {
        return this.http.get<ScheduleResponse[]>(`${this.apiUrl}/users/${userId}/schedules`);
    }
}