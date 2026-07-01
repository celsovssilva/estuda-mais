import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// O decorator @Injectable diz ao Angular que este serviço pode ser usado (injetado) em qualquer lugar do app
@Injectable({
    providedIn: 'root' // Significa que ele nasce junto com a aplicação e fica disponível globalmente
})
export class AuthService {
    // URL base que aponta para o AuthController do seu Spring Boot
    private apiUrl = 'http://localhost:8080/api/auth';

    // Injeta o cliente HTTP do Angular para fazer requisições (POST, GET, etc.)
    private http = inject(HttpClient);

    // Método que a tela de Login vai chamar
    login(credentials: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/login`, credentials);
    }

    // Método que a tela de Cadastro vai chamar logo logo
    register(userData: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/register`, userData);
    }

    // Métodos utilitários para gerenciar o Token de segurança no navegador
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