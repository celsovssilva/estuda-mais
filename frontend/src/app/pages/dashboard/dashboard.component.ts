import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChecklistService } from '../../core/services/checklist/checklist.service';
import { ScheduleService } from '../../core/services/schedule/schedule.service'; // Ajuste o caminho se necessário
import { AuthService } from '../../core/services/auth/auth.services';       // Ajuste o caminho se necessário
import { RouterModule, Router} from '@angular/router';


@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule],
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
    private checklistService = inject(ChecklistService);
    private scheduleService = inject(ScheduleService);
    private authService = inject(AuthService);
    private router = inject(Router);

    // Mantemos o objeto de métricas para alimentar os cards do topo
    metrics = {
        completedTasks: 0,
        pendingTasks: 0,
        totalSchedules: 0
    };

    // Mantemos os dados do perfil
    profileData = {
        name: '',
        email: '',
        password: ''
    };

    isSavingProfile = false;

    ngOnInit(): void {
        this.loadDashboardMetrics();
        // Aqui você pode chamar a função que carrega os dados iniciais do perfil se tiver, ex: this.loadUserProfile();
    }

    // Mantemos essa função porque ela calcula os números dos cards lendo o banco de dados
    loadDashboardMetrics(): void {
        this.checklistService.getTasksByUser().subscribe({
            next: (res: any) => {
                const list = Array.isArray(res) ? res : [];
                this.metrics.completedTasks = list.filter((t: any) => t.completed).length;
                this.metrics.pendingTasks = list.filter((t: any) => !t.completed).length;
            },
            error: (err) => console.error('Erro ao carregar métricas de tarefas:', err)
        });

        this.scheduleService.getSchedulesByUser().subscribe({
            next: (res: any) => {
                this.metrics.totalSchedules = Array.isArray(res) ? res.length : 0;
            },
            error: (err) => console.error('Erro ao carregar métricas da agenda:', err)
        });
    }

    // Mantemos a função de atualizar o perfil que estruturamos antes
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
                alert('Perfil atualizado com sucesso! Por segurança, faça login novamente.');
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