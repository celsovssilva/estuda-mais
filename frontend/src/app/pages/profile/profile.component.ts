import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth/auth.services';
import { Router } from '@angular/router';
import { NavbarComponent } from "../../app/shared/navbar/navbar.component";

export interface ToastNotification {
    message: string;
    type: 'error' | 'success' | 'warning';
}

@Component({
    selector: 'app-perfil',
    standalone: true,
    imports: [CommonModule, FormsModule, NavbarComponent],
    templateUrl: './profile.component.html',
    styleUrls: ['./profile.component.css']
})
export class PerfilComponent {
    private authService = inject(AuthService);
    private router = inject(Router);

    profileData = {
        name: '',
        email: '',
        password: ''
    };

    isSavingProfile = false;


    toast: ToastNotification | null = null;
    private toastTimeout: any;



    private extractErrorMessage(err: any, fallbackMessage: string): string {
        if (typeof err?.error === 'string') return err.error;
        return err?.error?.message || err?.error?.error || err?.message || fallbackMessage;
    }

    showToast(message: string, type: 'error' | 'success' | 'warning' = 'error'): void {
        this.toast = { message, type };
        if (this.toastTimeout) clearTimeout(this.toastTimeout);
        this.toastTimeout = setTimeout(() => {
            this.toast = null;
        }, 4000);
    }

    closeToast(): void {
        this.toast = null;
    }



    submitProfileUpdate(): void {
        const nameTrimmed = this.profileData.name?.trim();
        const emailTrimmed = this.profileData.email?.trim();
        const passwordTrimmed = this.profileData.password?.trim();

        if (!nameTrimmed || !emailTrimmed || !passwordTrimmed) {
            this.showToast('Por favor, preencha todos os campos (Nome, E-mail e Nova Senha).', 'warning');
            return;
        }

        if (passwordTrimmed.length < 6) {
            this.showToast('A nova senha deve conter no mínimo 6 caracteres.', 'warning');
            return;
        }

        const updatePayload = {
            name: nameTrimmed,
            email: emailTrimmed,
            password: passwordTrimmed
        };

        this.isSavingProfile = true;

        this.authService.updateProfile(updatePayload).subscribe({
            next: () => {
                this.isSavingProfile = false;
                this.showToast('Perfil atualizado com sucesso! Redirecionando para o login...', 'success');

                setTimeout(() => {
                    localStorage.clear();
                    this.router.navigate(['/login']);
                }, 1800);
            },
            error: (err) => {
                this.isSavingProfile = false;
                console.error('Erro na requisição:', err);
                const msg = this.extractErrorMessage(err, 'Falha ao atualizar cadastro.');
                this.showToast(msg, 'error');
            }
        });
    }
}