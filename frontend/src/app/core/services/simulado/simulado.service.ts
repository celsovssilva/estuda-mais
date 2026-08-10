import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Questao, ResultadoSimulado } from '../../models/simulado.models';
import {environment} from "../../../../environments/environment.prod";

@Injectable({
    providedIn: 'root'
})
export class SimuladoService {
    private http = inject(HttpClient);
    private apiUrl = `${environment.apiUrl}/api/simulados`;

    obterQuestoes(ano?: number | null, disciplina?: string | null): Observable<Questao[]> {
        let params = new HttpParams();
        if (ano) params = params.set('ano', ano.toString());
        if (disciplina) params = params.set('disciplina', disciplina);

        return this.http.get<Questao[]>(`${this.apiUrl}/simulados/questoes`, { params });
    }

    finalizarSimulado(payload: any): Observable<ResultadoSimulado> {
        return this.http.post<ResultadoSimulado>(`${this.apiUrl}/simulados/finalizar`, payload);
    }
}