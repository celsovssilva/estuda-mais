import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
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
    private route = inject(ActivatedRoute);

    // Dados da prova
    questoes: Questao[] = [];
    indiceAtual: number = 0;
    respostasAluno: Map<string, string> = new Map();
    anoSelecionado: number = 2023;

    // Estados da tela
    carregando: boolean = true;
    erroCarregamento: boolean = false;
    resultadoFinal: ResultadoSimulado | null = null;

    // Filtro para o gabarito final
    filtroAtual: 'TODAS' | 'ACERTOS' | 'ERROS' = 'TODAS';

    ngOnInit(): void {
        // Captura o ano passado na URL (ex: /simulado?ano=2022). Se não passar nada, assume 2023.
        const paramAno = this.route.snapshot.queryParamMap.get('ano');
        if (paramAno && !isNaN(Number(paramAno))) {
            this.anoSelecionado = Number(paramAno);
        }

        this.carregarQuestoes(this.anoSelecionado);
    }

    carregarQuestoes(ano: number): void {
        this.carregando = true;
        this.erroCarregamento = false;

        this.simuladoService.obterQuestoes(ano).subscribe({
            next: (dados) => {
                this.questoes = dados || [];
                this.carregando = false;
            },
            error: (err) => {
                console.error('Erro ao carregar questões:', err);
                this.carregando = false;
                this.erroCarregamento = true;
            }
        });
    }

    get questaoAtual(): Questao | null {
        return this.questoes.length > 0 ? this.questoes[this.indiceAtual] : null;
    }

    selecionarResposta(alternativa: string): void {
        if (this.questaoAtual) {
            this.respostasAluno.set(this.questaoAtual.id, alternativa);
        }
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
            error: (err) => console.error('Erro ao finalizar simulado:', err)
        });
    }

    // ===== LÓGICA DE FILTRAGEM DO GABARITO DETALHADO =====

    setFiltro(filtro: 'TODAS' | 'ACERTOS' | 'ERROS'): void {
        this.filtroAtual = filtro;
    }

    get gabaritoFiltrado() {
        if (!this.resultadoFinal?.gabarito) return [];

        if (this.filtroAtual === 'ACERTOS') {
            return this.resultadoFinal.gabarito.filter(item => item.acertou);
        }
        if (this.filtroAtual === 'ERROS') {
            return this.resultadoFinal.gabarito.filter(item => !item.acertou);
        }
        return this.resultadoFinal.gabarito;
    }
}