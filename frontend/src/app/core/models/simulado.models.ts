export interface Questao {
    id: string;
    ano: number;
    disciplina: string;
    imagem?: string;
    enunciado: string;
    alternativas: string[];
}

export interface RespostaItem {
    questaoId: string;
    alternativaEscolhida: string;
}

export interface EnviarSimuladoRequest {
    usuarioId: number;
    respostas: RespostaItem[];
}

export interface GabaritoItem {
    questaoId: string;
    enunciado: string;
    alternativaEscolhida: string;
    respostaCorreta: string;
    acertou: boolean;
}

export interface ResultadoSimulado {
    simuladoId: number;
    notaTRI: number;
    totalAcertos: number;
    totalQuestoes: number;
    porcentagemAcerto: number;
    gabarito: GabaritoItem[];
}