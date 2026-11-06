import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// DTOs que espelham exatamente os 'records' que criámos no Java
export interface VideoRequest {
    url: string;
}

export interface ResumoResponse {
    resumo: string;
}

@Injectable({
    providedIn: 'root'
})
export class VideoConsumoService {

    private readonly apiUrl = 'http://localhost:8080/api/resumo';

    constructor(private http: HttpClient) {}

    resumir(url: string): Observable<ResumoResponse> {
        const request: VideoRequest = { url };

        return this.http.post<ResumoResponse>(`${this.apiUrl}/resumir`, request);
    }
}