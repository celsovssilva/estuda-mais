import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SimuladoService } from '../../core/services/simulado/simulado.service';
import { Questao, RespostaItem, ResultadoSimulado } from '../../core/models/simulado.models';

@Component({
    selector: 'app-simulado-execucao',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './simulado.component.html',
    styleUrl: './simulado.component.css'
})
export class SimuladoExecucaoComponent implements OnInit {
    private simuladoService = inject(SimuladoService);

    questoes: Questao[] = [];
    indiceAtual: number = 0;
    respostasAluno: Map<string, string> = new Map();

    carregando: boolean = true;
    resultadoFinal: ResultadoSimulado | null = null;

    ngOnInit(): void {
        // Busca todas as questões (ou passe a disciplina como parâmetro)
        this.simuladoService.obterQuestoes().subscribe({
            next: (dados) => {
                this.questoes = dados;
                this.carregando = false;
            },
            error: (err) => console.error('Erro ao carregar questões', err)
        });
    }

    get questaoAtual(): Questao {
        return this.questoes[this.indiceAtual];
    }

    selecionarResposta(alternativa: string): void {
        this.respostasAluno.set(this.questaoAtual.id, alternativa);
    }

    proximaQuestao(): void {
        if (this.indiceAtual < this.questoes.length - 1) {
            this.indiceAtual++;
        }
    }

    questaoAnterior(): void {
        if (this.indiceAtual > 0) {
            this.indiceAtual--;
        }
    }

    finalizarSimulado(): void {
        const respostasArray: RespostaItem[] = Array.from(this.respostasAluno.entries()).map(
            ([questaoId, alternativaEscolhida]) => ({ questaoId, alternativaEscolhida })
        );

        const payload = {
            usuarioId: 1, // ID temporário do usuário logado
            respostas: respostasArray
        };

        this.simuladoService.finalizarSimulado(payload).subscribe({
            next: (res) => {
                this.resultadoFinal = res;
            },
            error: (err) => console.error('Erro ao finalizar simulado', err)
        });
    }
}