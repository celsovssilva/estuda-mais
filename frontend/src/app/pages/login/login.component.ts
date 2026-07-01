import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth/auth.services';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RouterModule],
    templateUrl: './login.component.html',
    styleUrl:'./login.component.css'
})
export class LoginComponent {
    // Injeção de dependências moderna do Angular (substitui o construtor pesado)
    private fb = inject(FormBuilder);
    private authService = inject(AuthService);
    private router = inject(Router);

    // Criando o espelho do formulário com suas validações
    loginForm: FormGroup = this.fb.group({
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(6)]]
    });

    errorMessage: string | null = null;
    isLoading = false;

    onSubmit(): void {
        // Se o usuário tentar enviar algo errado, marcamos os campos como tocados para exibir os erros visuais
        if (this.loginForm.invalid) {
            this.loginForm.markAllAsTouched();
            return;
        }

        this.isLoading = true;
        this.errorMessage = null;

        // Dispara os dados para o serviço
        this.authService.login(this.loginForm.value).subscribe({
            next: (response) => {
                this.authService.saveToken(response.token);
                this.isLoading = false;
                this.router.navigate(['/dashboard']); // Avança para o painel principal
            },
            error: (err) => {
                this.isLoading = false;
                // Captura a mensagem de erro tratada que criamos lá nas Exceptions do seu Spring Boot
                this.errorMessage = err.error?.message || 'Erro ao realizar login. Verifique suas credenciais.';
            }
        });
    }
}