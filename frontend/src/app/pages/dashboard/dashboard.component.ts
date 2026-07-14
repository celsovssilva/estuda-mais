import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ScheduleService } from '../../core/services/schedule/schedule.service';
import { ScheduleResponse } from '../../core/models/schedule.models';
import { AuthService } from '../../core/services/auth/auth.services';
import { RouterModule, Router } from '@angular/router';
import { NavbarComponent } from "../../app/shared/navbar/navbar.component";

@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule, NavbarComponent],
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
    private scheduleService = inject(ScheduleService);
    private authService = inject(AuthService);
    private router = inject(Router);

    metrics = {
        completedTasks: 0,
        pendingTasks: 0,
        totalSchedules: 0
    };

    profileData = {
        name: '',
        email: '',
        password: ''
    };

    isSavingProfile = false;

    ngOnInit(): void {
        this.loadDashboardMetrics();
    }

    loadDashboardMetrics(): void {
        this.scheduleService.getSchedulesByUser().subscribe({
            next: (res: ScheduleResponse[]) => {
                const all = Array.isArray(res) ? res : [];
                this.metrics.totalSchedules = all.length;
                this.metrics.completedTasks = all.filter(s => s.completed).length;
                this.metrics.pendingTasks = all.filter(s => !s.completed).length;
            },
            error: (err) => console.error('Erro ao carregar métricas da agenda:', err)
        });
    }

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