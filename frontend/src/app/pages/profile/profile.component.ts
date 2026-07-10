import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth/auth.services'; // Ajuste o caminho do seu AuthService
import { Router } from '@angular/router';
import {NavbarComponent} from "../../app/shared/navbar/navbar.component";

@Component({
    selector: 'app-perfil',
    standalone: true,
    imports: [CommonModule, FormsModule,NavbarComponent],
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

    submitProfileUpdate(): void {
        const nameTrimmed = this.profileData.name?.trim();
        const emailTrimmed = this.profileData.email?.trim();
        const passwordTrimmed = this.profileData.password?.trim();

        if (!nameTrimmed || !emailTrimmed || !passwordTrimmed) {
            alert('Por favor, preencha todos os campos (Nome, E-mail e Nova Senha).');
            return;
        }

        if (passwordTrimmed.length < 6) {
            alert('A nova senha deve conter no mínimo 6 caracteres.');
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
                alert('Perfil updated com sucesso! Por segurança, faça login novamente.');
                localStorage.clear();
                this.router.navigate(['/login']);
            },
            error: (err) => {
                this.isSavingProfile = false;
                console.error('Erro na requisição:', err);
                alert('Falha ao atualizar cadastro.');
            }
        });
    }
}