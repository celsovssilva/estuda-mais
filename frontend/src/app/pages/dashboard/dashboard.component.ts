import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ScheduleService } from '../../core/services/schedule/schedule.service';
import {CategoryMetric, ScheduleResponse} from '../../core/models/schedule.models';
import { AuthService } from '../../core/services/auth/auth.services';
import { RouterModule, Router } from '@angular/router';
import { NavbarComponent } from "../../app/shared/navbar/navbar.component";

interface DaySummary {
    label: string;
    dateStr: string;
    totalMinutes: number;
    formatted: string;
    isToday: boolean;
    byCategory: Record<string, number>;
}

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

    // Resumo do dia atual
    todayFormatted: string = '0h 0min';

    // Resumo da semana (Dom a Sáb), um item por dia
    weekSummary: DaySummary[] = [];
    weekDayLabels: string[] = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'];
    weekTotalsByCategory: Record<string, number> = {};


    profileData = {
        name: '',
        email: '',
        password: ''
    };

    isSavingProfile = false;
    categoryMetrics: CategoryMetric[] = [];
    categoryLabels: Record<string, string> = {
        ACADEMIA: '🏋️ Academia',
        ESTUDOS: '📚 Estudos',
        CUIDADO_PESSOAL: '🧘 Cuidado Pessoal',
        OUTROS: '📌 Outros'
    };
    categoryKeys: string[] = Object.keys(this.categoryLabels);
    ngOnInit(): void {
        this.loadDashboardMetrics();
        this.loadCategoryMetrics();
    }
    loadCategoryMetrics(): void {
        this.scheduleService.getMetrics().subscribe({
            next: (res: any[]) => {
                const rawMetrics = res || [];

                this.categoryMetrics = rawMetrics.map(item => {

                    const catKey = (item.category || item.categoryName || item.name || 'OUTROS').toString().toUpperCase();

                    return {
                        category: catKey,
                        total: item.total || 0,
                        completed: item.completed || 0,
                        percentage: item.percentage || 0
                    };
                });
            },
            error: (err) => console.error('Erro ao carregar métricas por categoria:', err)
        });
    }

    loadDashboardMetrics(): void {
        this.scheduleService.getSchedulesByUser().subscribe({
            next: (res: ScheduleResponse[]) => {
                const all = Array.isArray(res) ? res : [];
                this.metrics.totalSchedules = all.length;
                this.metrics.completedTasks = all.filter(s => s.completed).length;
                this.metrics.pendingTasks = all.filter(s => !s.completed).length;

                this.computeTimeSummaries(all);
            },
            error: (err) => console.error('Erro ao carregar métricas da agenda:', err)
        });
    }

    // Converte "HH:mm" ou "HH:mm:ss" em minutos desde a meia-noite
    private timeToMinutes(time: string): number {
        const [h, m] = time.split(':').map(Number);
        return (h * 60) + m;
    }

    // Duração em minutos entre startTime e endTime. Retorna 0 se inválido.
    private durationMinutes(schedule: ScheduleResponse): number {
        if (!schedule.startTime || !schedule.endTime) return 0;
        const start = this.timeToMinutes(schedule.startTime);
        const end = this.timeToMinutes(schedule.endTime);
        const diff = end - start;
        return diff > 0 ? diff : 0;
    }

    formatMinutes(totalMinutes: number): string {
        const hours = Math.floor(totalMinutes / 60);
        const minutes = totalMinutes % 60;
        return `${hours}h ${minutes}min`;
    }

    private computeTimeSummaries(all: ScheduleResponse[]): void {
        const completedWithTime = all.filter(s => s.completed && s.startTime && s.endTime);

        const today = new Date();
        const todayStr = today.toISOString().split('T')[0];

        // --- Resumo do dia ---
        const todayMinutes = completedWithTime
            .filter(s => s.targetDate === todayStr)
            .reduce((sum, s) => sum + this.durationMinutes(s), 0);
        this.todayFormatted = this.formatMinutes(todayMinutes);

        // --- Resumo da semana (domingo a sábado da semana atual) ---
        const dayOfWeek = today.getDay(); // 0 = domingo
        const sunday = new Date(today);
        sunday.setDate(today.getDate() - dayOfWeek);

        const days: DaySummary[] = [];
        for (let i = 0; i < 7; i++) {
            const d = new Date(sunday);
            d.setDate(sunday.getDate() + i);
            const dateStr = d.toISOString().split('T')[0];

            const dayMinutes = completedWithTime
                .filter(s => s.targetDate === dateStr)
                .reduce((sum, s) => sum + this.durationMinutes(s), 0);

            const byCategory = completedWithTime
                .filter(s => s.targetDate === dateStr)
                .reduce((acc, item) => {
                    const cat = item.category;
                    const minutos = this.durationMinutes(item);
                    acc[cat] = (acc[cat] ?? 0) + minutos; // preencha aqui
                    return acc;
                }, {} as Record<string, number>);

            days.push({
                label: this.weekDayLabels[i],
                dateStr,
                totalMinutes: dayMinutes,
                formatted: this.formatMinutes(dayMinutes),
                isToday: dateStr === todayStr,
                byCategory: byCategory
            });
        }
        this.weekSummary = days;
        const weekTotalsByCategory:Record<string, number>={};

        for(const day of days){
            for(const cat in day.byCategory){
                weekTotalsByCategory[cat] = (weekTotalsByCategory[cat]  ?? 0) +  day.byCategory[cat];

              }

        }
        this.weekTotalsByCategory= weekTotalsByCategory;
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