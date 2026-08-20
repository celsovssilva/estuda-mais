import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

import { SimuladoService } from '../../core/services/simulado/simulado.service';
import { Questao, RespostaItem, ResultadoSimulado, EnviarSimuladoRequest } from '../../core/models/simulado.models';
import { NavbarComponent } from "../../app/shared/navbar/navbar.component";

@Component({
    selector: 'app-simulado-execucao',
    standalone: true,
    imports: [CommonModule, FormsModule, NavbarComponent],
    templateUrl: './simulado.component.html',
    styleUrl: './simulado.component.css'
})
export class SimuladoExecucaoComponent implements OnInit, OnDestroy {
    private simuladoService = inject(SimuladoService);
    private route = inject(ActivatedRoute);
    private router = inject(Router);
    private sanitizer = inject(DomSanitizer);

    simuladoId: number = 1;
    questoes: Questao[] = [];
    indiceAtual: number = 0;
    respostasAluno: Map<string, string> = new Map();

    tempoDecorrido: number = 0;
    private timerInterval: any = null;
    isPausado: boolean = false;

    anoSelecionado: number = 2022;
    disciplinaSelecionada: string | null = null;
    diaSelecionado: string | null = null;
    idiomaSelecionado: string | null = null;

    carregando: boolean = false;
    erroCarregamento: boolean = false;
    resultadoFinal: ResultadoSimulado | null = null;

    filtroAtual: 'TODAS' | 'ACERTOS' | 'ERROS' = 'TODAS';
    alternativasEliminadas: Map<string, Set<number>> = new Map();

    painelGabaritoAberto: boolean = false;
    historicoSimulados: ResultadoSimulado[] = [];

    ngOnInit(): void {
        const paramAno = this.route.snapshot.queryParamMap.get('ano');
        if (paramAno && !isNaN(Number(paramAno))) {
            this.anoSelecionado = Number(paramAno);

        }
        this.verificarSimuladoPendente();
        this.carregarHistorico();
    }

    ngOnDestroy(): void {
        this.pararTimer();
    }

    verificarSimuladoPendente(): void {
        this.simuladoService.buscarPendentes().subscribe({
            next: (pendente: any) => {
                if (pendente) {
                    this.simuladoId = pendente.id ?? 1;
                    this.indiceAtual = pendente.indiceAtual ?? 0;
                    this.tempoDecorrido = pendente.tempoDecorrido ?? 0;

                    if (pendente.ano) this.anoSelecionado = pendente.ano;
                    if (pendente.disciplina) this.disciplinaSelecionada = pendente.disciplina;
                    if (pendente.dia) this.diaSelecionado = pendente.dia;
                    if (pendente.idioma) this.idiomaSelecionado = pendente.idioma;

                    const respostas = pendente.respostas || pendente.respostaAlunos || [];
                    respostas.forEach((r: any) => {
                        const qId = String(r.questaoId || r.questionId);
                        const alt = r.alternativaEscolhida || r.opcaoSelecionada;
                        if (qId && alt) {
                            this.respostasAluno.set(qId, alt);
                        }
                    });

                    this.iniciarSimulado(true);
                }
            },
            error: () => {}
        });
    }

    iniciarTimer(): void {
        this.pararTimer();
        this.timerInterval = setInterval(() => {
            this.tempoDecorrido++;
        }, 1000);
    }

    pararTimer(): void {
        if (this.timerInterval) {
            clearInterval(this.timerInterval);
            this.timerInterval = null;
        }
    }

    pausarSimulado(): void {
        this.pararTimer();
        this.isPausado = true;
    }

    retomarSimulado(): void {
        this.iniciarTimer();
        this.isPausado = false;
    }


    reiniciarTentativa(): void {
        if (!confirm('Isso vai apagar suas respostas e reiniciar o cronômetro. Deseja continuar?')) {
            return;
        }
        this.pararTimer();
        this.tempoDecorrido = 0;
        this.respostasAluno.clear();
        this.alternativasEliminadas.clear();
        this.indiceAtual = 0;
        this.isPausado = false;
        this.iniciarTimer();
    }

    get tempoFormatado(): string {
        const hrs = Math.floor(this.tempoDecorrido / 3600);
        const mins = Math.floor((this.tempoDecorrido % 3600) / 60);
        const segs = this.tempoDecorrido % 60;

        const hStr = hrs < 10 ? `0${hrs}` : `${hrs}`;
        const mStr = mins < 10 ? `0${mins}` : `${mins}`;
        const sStr = segs < 10 ? `0${segs}` : `${segs}`;

        return hrs > 0 ? `${hStr}:${mStr}:${sStr}` : `${mStr}:${sStr}`;
    }

    iniciarSimulado(isRetomada: boolean = false): void {
        this.carregarQuestoes(this.anoSelecionado, this.disciplinaSelecionada, this.diaSelecionado, this.idiomaSelecionado, isRetomada);
    }

    carregarQuestoes(
        ano: number,
        disciplina: string | null = null,
        dia: string | null = null,
        idioma: string | null = null,
        isRetomada: boolean = false
    ): void {
        this.carregando = true;
        this.erroCarregamento = false;

        this.simuladoService.obterQuestoes(ano, disciplina, dia, idioma).subscribe({
            next: (dados) => {
                this.questoes = dados || [];
                if (this.questoes.length === 0) {
                    alert('Nenhuma questão encontrada para estes filtros no banco de dados!');
                } else {
                    this.iniciarTimer();
                }
                this.carregando = false;
            },
            error: (err) => {
                console.error('Erro ao carregar questões:', err);
                this.carregando = false;
                this.erroCarregamento = true;
            }
        });
    }

    finalizarSimulado(): void {
        this.pararTimer();

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

    toggleEliminada(index: number, event: Event): void {
        event.stopPropagation();
        if (!this.questaoAtual) return;
        const id = this.questaoAtual.id;

        if (!this.alternativasEliminadas.has(id)) {
            this.alternativasEliminadas.set(id, new Set<number>());
        }

        const eliminadas = this.alternativasEliminadas.get(id)!;
        if (eliminadas.has(index)) {
            eliminadas.delete(index);
        } else {
            eliminadas.add(index);
        }
    }

    isEliminada(index: number): boolean {
        if (!this.questaoAtual) return false;
        const eliminadas = this.alternativasEliminadas.get(this.questaoAtual.id);
        return eliminadas ? eliminadas.has(index) : false;
    }

    letraAlternativa(index: number): string {
        return String.fromCharCode(65 + index);
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

    get questaoAtual(): Questao | null {
        return this.questoes.length > 0 ? this.questoes[this.indiceAtual] : null;
    }

    get totalAcertos(): number {
        if (!this.resultadoFinal?.gabarito) return 0;
        return this.resultadoFinal.gabarito.filter(item => item.acertou).length;
    }

    get totalRespondidas(): number {
        return this.respostasAluno.size;
    }

    selecionarResposta(alternativa: string): void {
        if (this.questaoAtual && !this.isPausado) {
            this.respostasAluno.set(this.questaoAtual.id, alternativa);
        }
    }

    isRespostaSelecionada(alternativa: string): boolean {
        if (!this.questaoAtual) return false;
        return this.respostasAluno.get(this.questaoAtual.id) === alternativa;
    }


    letraMarcadaNaQuestao(index: number): string | null {
        const questao = this.questoes[index];
        if (!questao) return null;

        const resposta = this.respostasAluno.get(questao.id);
        if (!resposta) return null;

        const idxAlternativa = questao.alternativas?.indexOf(resposta);
        if (idxAlternativa === undefined || idxAlternativa < 0) return null;

        return this.letraAlternativa(idxAlternativa);
    }


    respondeuQuestao(index: number): boolean {
        const questao = this.questoes[index];
        return !!questao && this.respostasAluno.has(questao.id);
    }


    statusGabarito(index: number): 'atual' | 'respondida' | 'pendente' {
        if (index === this.indiceAtual) return 'atual';
        return this.respondeuQuestao(index) ? 'respondida' : 'pendente';
    }


    irParaQuestao(index: number): void {
        if (this.isPausado) return;
        if (index >= 0 && index < this.questoes.length) {
            this.indiceAtual = index;
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


    avancarOuEnviar(): void {
        if (this.indiceAtual < this.questoes.length - 1) {
            this.proximaQuestao();
        } else {
            this.finalizarSimulado();
        }
    }

    toggleGabarito(): void {
        this.painelGabaritoAberto = !this.painelGabaritoAberto;
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

    carregarHistorico(): void {
        this.simuladoService.buscarHistorico().subscribe({
            next: (dados) => {
                this.historicoSimulados = dados;
            },
            error: (err) => {
                console.error('Erro ao carregar histórico:', err);
            }
        });
    }

    reiniciarSimulado(): void {
        this.pararTimer();
        this.isPausado = false;
        this.resultadoFinal = null;
        this.questoes = [];
        this.indiceAtual = 0;
        this.tempoDecorrido = 0;
        this.respostasAluno.clear();
        this.alternativasEliminadas.clear();
    }
}