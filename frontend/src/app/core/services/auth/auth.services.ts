import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from "../../../../environments/environment.development";

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private apiUrl = `${environment.apiUrl}/api/auth`;

    private http = inject(HttpClient);

    login(credentials: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/login`, credentials);
    }

    register(userData: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/register`, userData);
    }

    updateProfile(profileData: any): Observable<any> {
        return this.http.put(`${this.apiUrl}/update`, profileData);
    }

    saveToken(token: string): void {
        localStorage.setItem('@EstudaMais:token', token);
    }

    getToken(): string | null {
        return localStorage.getItem('@EstudaMais:token');
    }

    logout(): void {
        localStorage.removeItem('@EstudaMais:token');
    }
    private decodeToken(token: string): any {
        try {
            const payload = token.split('.')[1];
            const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(
                atob(base64).split('').map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)).join('')
            );
            return JSON.parse(jsonPayload);
        } catch {
            return null;
        }
    }

    getRole(): string | null {
        const token = this.getToken();
        if (!token) return null;
        return this.decodeToken(token)?.role ?? null;
    }

    isAdmin(): boolean {
        return this.getRole() === 'ADMIN';
    }
}