import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';



@Injectable({
    providedIn: 'root'
})
export class AuthService {

    private apiUrl: string=  'http://localhost:8080/api/auth';


    private http = inject(HttpClient);


    login(credentials: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/login`, credentials);
    }


    register(userData: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/register`, userData);
    }
    updateProfile(profileData: any): Observable<any> {
        return this.http.put(`${this.apiUrl}/api/auth/update`, profileData);
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
}