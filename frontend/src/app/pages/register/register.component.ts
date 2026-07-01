import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth/auth.services';

@Component({
    selector: 'app-register',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RouterModule],
    templateUrl: './register.component.html',
    styleUrl:'./register.component.css'
})
export class RegisterComponent {
    private fb = inject(FormBuilder);
    private authService = inject(AuthService);
    private router = inject(Router);

    // Formulário Reativo capturando Nome, E-mail e Senha
    registerForm: FormGroup = this.fb.group({
        name: ['', [Validators.required, Validators.minLength(3)]],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(6)]]
    });

    errorMessage: string | null = null;
    isLoading = false;

    onSubmit(): void {
        if (this.registerForm.invalid) {
            this.registerForm.markAllAsTouched();
            return;
        }

        this.isLoading = true;
        this.errorMessage = null;

        // Dispara os dados para o endpoint /api/auth/register do Spring
        this.authService.register(this.registerForm.value).subscribe({
            next: () => {
                this.isLoading = false;
                // Cadastro feito com sucesso! Redireciona para o login
                alert('Conta criada com sucesso! Faça seu login.');
                this.router.navigate(['/login']);
            },
            error: (err) => {
                this.isLoading = false;
                // Captura mensagens do backend (Ex: "Este e-mail já está cadastrado")
                this.errorMessage = err.error?.message || 'Erro ao realizar o cadastro. Tente novamente.';
            }
        });
    }
}