import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { VideoConsumoService, ResumoResponse } from '../../core/services/resumo/videoConsumoService';

@Component({
    selector: 'app-video-consumo',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './video-consumo.component.html',
    styleUrls: ['./video-consumo.component.css']
})
export class VideoConsumoComponent {
    urlInput: string = '';
    textoResumo: string | null = null;
    erro: string | null = null;
    carregando: boolean = false;

    constructor(private videoConsumoService: VideoConsumoService) {}

    gerarResumo(): void {
        if (!this.urlInput || this.urlInput.trim() === '') {
            return;
        }

        this.carregando = true;
        this.erro = null;
        this.textoResumo = null;

        this.videoConsumoService.resumir(this.urlInput).subscribe({
            next: (response: ResumoResponse) => {

                this.textoResumo = response?.resumoGerado ?? response?.resumo ?? null;

                if (!this.textoResumo) {
                    this.erro = 'O servidor não retornou um resumo.';
                }
                this.carregando = false;
            },
            error: (err) => {
                console.error('Erro ao comunicar com o backend:', err);
                this.erro = 'Não foi possível gerar o resumo. Verifique se o link está correto e se o servidor Java está a correr.';
                this.carregando = false;
            }
        });
    }
}