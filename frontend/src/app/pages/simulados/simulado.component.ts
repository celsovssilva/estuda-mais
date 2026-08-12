import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

import { SimuladoService } from '../../core/services/simulado/simulado.service';
import { Questao, RespostaItem, ResultadoSimulado, EnviarSimuladoRequest } from '../../core/models/simulado.models';

@Component({
    selector: 'app-simulado-execucao',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './simulado.component.html',
    styleUrl: './simulado.component.css'
})
export class SimuladoExecucaoComponent implements OnInit {
    private simuladoService = inject(SimuladoService);
    private route = inject(ActivatedRoute);
    private sanitizer = inject(DomSanitizer);

    questoes: Questao[] = [];
    indiceAtual: number = 0;
    respostasAluno: Map<string, string> = new Map();

    anoSelecionado: number = 2022;
    disciplinaSelecionada: string | null = null;
    diaSelecionado: string | null = null;
    idiomaSelecionado: string | null = null;

    carregando: boolean = false;
    erroCarregamento: boolean = false;
    resultadoFinal: ResultadoSimulado | null = null;

    filtroAtual: 'TODAS' | 'ACERTOS' | 'ERROS' = 'TODAS';

    ngOnInit(): void {
        const paramAno = this.route.snapshot.queryParamMap.get('ano');
        if (paramAno && !isNaN(Number(paramAno))) {
            this.anoSelecionado = Number(paramAno);
        }
    }

    formatarEnunciado(texto: string | undefined): SafeHtml {
        if (!texto) return '';

        let html = texto.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');

        html = html.replace(
            /!\[.*?\]\((.*?)\)/g,
            '<div class="container-imagem-questao"><img src="$1" alt="Imagem de apoio" class="imagem-questao" /></div>'
        );

        return this.sanitizer.bypassSecurityTrustHtml(html);
    }

    iniciarSimulado(): void {
        this.carregarQuestoes(this.anoSelecionado, this.disciplinaSelecionada, null, null);
    }

    carregarQuestoes(
        ano: number,
        disciplina: string | null = null,
        dia: string | null = null,
        idioma: string | null = null
    ): void {
        this.carregando = true;
        this.erroCarregamento = false;

        this.simuladoService.obterQuestoes(ano, disciplina, dia, idioma).subscribe({
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

    get totalAcertos(): number {
        if (!this.resultadoFinal?.gabarito) return 0;
        return this.resultadoFinal.gabarito.filter(item => item.acertou).length;
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

        const payload: EnviarSimuladoRequest = {
            usuarioId: 1,
            respostas: respostasArray
        };

        this.simuladoService.finalizarSimulado(payload).subscribe({
            next: (res) => {
                this.resultadoFinal = res;
            },
            error: (err) => console.error('Erro ao finalizar simulado:', err)
        });
    }

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

    reiniciarSimulado(): void {
        this.resultadoFinal = null;
        this.questoes = [];
        this.indiceAtual = 0;
        this.respostasAluno.clear();
    }
}