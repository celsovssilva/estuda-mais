import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Questao, EnviarSimuladoRequest, ResultadoSimulado } from '../../models/simulado.models';
import {environment} from "../../../../environments/environment.prod";

@Injectable({
    providedIn: 'root'
})
export class SimuladoService {
    private http = inject(HttpClient);

    // Em desenvolvimento usamos localhost; em produção será a URL do Render
    private apiUrl = `${environment.apiUrl}/api/simulados`;

    obterQuestoes(disciplina?: string): Observable<Questao[]> {
        const url = disciplina ? `${this.apiUrl}/questoes?disciplina=${disciplina}` : `${this.apiUrl}/questoes`;
        return this.http.get<Questao[]>(url);
    }

    finalizarSimulado(payload: EnviarSimuladoRequest): Observable<ResultadoSimulado> {
        return this.http.post<ResultadoSimulado>(`${this.apiUrl}/finalizar`, payload);
    }
}