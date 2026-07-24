import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ScheduleService } from '../../core/services/schedule/schedule.service';
import { CategoryMetric, ScheduleRequest, ScheduleResponse } from '../../core/models/schedule.models';
import { NavbarComponent } from "../../app/shared/navbar/navbar.component";

type DayStatus = 'none' | 'red' | 'orange' | 'green';

export interface ToastNotification {
    message: string;
    type: 'error' | 'success' | 'warning';
}

@Component({
    selector: 'app-agenda',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule, NavbarComponent],
    templateUrl: './schedule.component.html',
    styleUrls: ['./schedule.component.css']
})
export class AgendaComponent implements OnInit {
    private scheduleService = inject(ScheduleService);

    allSchedules: ScheduleResponse[] = [];
    filteredSchedules: ScheduleResponse[] = [];

    formSchedule = {
        title: '',
        description: '',
        targetDate: '',
        type: 'DAY',
        startTime: '',
        endTime: '',
        category: 'OUTROS'
    };

    // Controle do ID para Edição
    editingScheduleId: number | null = null;

    currentYear: number = 2026;
    currentMonth: number = new Date().getMonth();
    selectedDateStr: string = '';
    viewMode: 'MONTH' | 'DAY' = 'MONTH';

    calendarWeeks: any[][] = [];
    weekDays: string[] = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'];
    monthNames: string[] = [
        'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
        'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
    ];

    selectedDayStatus: DayStatus = 'none';

    toast: ToastNotification | null = null;
    private toastTimeout: any;

    pendingDeleteId: number | null = null;

    ngOnInit(): void {
        const today = new Date();
        this.currentYear = today.getFullYear();
        this.selectedDateStr = today.toISOString().split('T')[0];
        this.formSchedule.targetDate = this.selectedDateStr;
        this.loadSchedules();
    }

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

    loadSchedules(): void {
        this.scheduleService.getSchedulesByUser().subscribe({
            next: (res: ScheduleResponse[]) => {
                this.allSchedules = res || [];
                this.buildMonthlyCalendar();
                this.filterSchedules();
            },
            error: (err) => {
                const msg = this.extractErrorMessage(err, 'Erro ao buscar compromissos.');
                this.showToast(msg, 'error');
            }
        });
    }

    private computeDayStatus(daySchedules: ScheduleResponse[]): DayStatus {
        if (daySchedules.length === 0) return 'none';
        const completedCount = daySchedules.filter(s => s.completed).length;
        if (completedCount === daySchedules.length) return 'green';
        if (completedCount > 0) return 'orange';
        return 'red';
    }

    buildMonthlyCalendar(): void {
        const firstDayIndex = new Date(this.currentYear, this.currentMonth, 1).getDay();
        const totalDays = new Date(this.currentYear, this.currentMonth + 1, 0).getDate();

        const daysArray: any[] = [];

        for (let i = 0; i < firstDayIndex; i++) {
            daysArray.push(null);
        }

        for (let day = 1; day <= totalDays; day++) {
            const dayString = String(day).padStart(2, '0');
            const monthString = String(this.currentMonth + 1).padStart(2, '0');
            const dateStr = `${this.currentYear}-${monthString}-${dayString}`;

            const daySchedules = this.allSchedules.filter(s => s.targetDate === dateStr);
            const status = this.computeDayStatus(daySchedules);

            daysArray.push({
                day,
                dateStr,
                hasEvents: daySchedules.length > 0,
                eventsCount: daySchedules.length,
                status
            });
        }

        this.calendarWeeks = [];
        while (daysArray.length > 0) {
            this.calendarWeeks.push(daysArray.splice(0, 7));
        }
    }

    filterSchedules(): void {
        let dayItems: ScheduleResponse[];

        if (this.viewMode === 'MONTH') {
            dayItems = this.allSchedules.filter(s => {
                if (!s.targetDate) return false;
                const d = new Date(s.targetDate + 'T00:00:00');
                return d.getFullYear() === this.currentYear && d.getMonth() === this.currentMonth;
            });
        } else {
            dayItems = this.allSchedules.filter(s => s.targetDate === this.selectedDateStr);
        }

        if (this.viewMode === 'DAY') {
            const dayOnlyItems = this.allSchedules.filter(s => s.targetDate === this.selectedDateStr);
            this.selectedDayStatus = this.computeDayStatus(dayOnlyItems);
            // No modo DIA ESPECÍFICO, mostra TODOS (inclusive os concluídos)
            this.filteredSchedules = dayItems;
        } else {
            this.selectedDayStatus = 'none';
            // No modo MÊS, mostra apenas os que estão a fazer (!completed)
            this.filteredSchedules = dayItems.filter(s => !s.completed);
        }
    }

    selectDay(dateStr: string): void {
        if (!dateStr) return;
        this.selectedDateStr = dateStr;
        this.formSchedule.targetDate = dateStr;
        this.viewMode = 'DAY';
        this.filterSchedules();
    }

    navigateMonth(offset: number): void {
        this.currentMonth += offset;
        if (this.currentMonth > 11) {
            this.currentMonth = 0;
            this.currentYear++;
        } else if (this.currentMonth < 0) {
            this.currentMonth = 11;
            this.currentYear--;
        }
        this.buildMonthlyCalendar();
        this.filterSchedules();
    }

    // Salva um NOVO agendamento ou ATUALIZA o existente
    saveSchedule(): void {
        if (!this.formSchedule.title.trim() || !this.formSchedule.targetDate) {
            this.showToast('Por favor, defina um Título e uma Data de Agendamento.', 'warning');
            return;
        }

        const payload: ScheduleRequest = {
            title: this.formSchedule.title.trim(),
            description: this.formSchedule.description.trim() || 'Sem descrição cadastrada.',
            targetDate: this.formSchedule.targetDate,
            type: this.formSchedule.type,
            startTime: this.formSchedule.startTime ? this.formSchedule.startTime : null,
            endTime: this.formSchedule.endTime ? this.formSchedule.endTime : null,
            category: this.formSchedule.category
        };

        if (this.editingScheduleId) {
            this.scheduleService.updateSchedule(this.editingScheduleId, payload).subscribe({
                next: () => {
                    this.showToast('Compromisso atualizado com sucesso!', 'success');
                    this.resetForm();
                    this.loadSchedules();
                },
                error: (err) => {
                    const msg = this.extractErrorMessage(err, 'Erro ao atualizar compromisso.');
                    this.showToast(msg, 'error');
                }
            });
        } else {
            this.scheduleService.createSchedule(payload).subscribe({
                next: () => {
                    this.showToast('Compromisso agendado com sucesso!', 'success');
                    this.resetForm();
                    this.loadSchedules();
                },
                error: (err) => {
                    const msg = this.extractErrorMessage(err, 'Ocorreu uma falha ao registrar o compromisso.');
                    this.showToast(msg, 'error');
                }
            });
        }
    }

    editSchedule(schedule: ScheduleResponse): void {
        this.editingScheduleId = schedule.id;
        this.formSchedule = {
            title: schedule.title,
            description: schedule.description === 'Sem descrição cadastrada.' ? '' : schedule.description,
            targetDate: schedule.targetDate,
            type: schedule.type,
            startTime: schedule.startTime || '',
            endTime: schedule.endTime || '',
            category: schedule.category || 'OUTROS'
        };
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }

    cancelEdit(): void {
        this.resetForm();
    }

    private resetForm(): void {
        this.editingScheduleId = null;
        this.formSchedule = {
            title: '',
            description: '',
            targetDate: this.selectedDateStr || new Date().toISOString().split('T')[0],
            type: 'DAY',
            startTime: '',
            endTime: '',
            category: 'OUTROS'
        };
    }

    toggleSchedule(schedule: ScheduleResponse): void {
        this.scheduleService.toggleSchedule(schedule.id).subscribe({
            next: (updated: ScheduleResponse) => {
                const idx = this.allSchedules.findIndex(s => s.id === updated.id);
                if (idx !== -1) {
                    this.allSchedules[idx] = updated;
                }
                this.buildMonthlyCalendar();
                this.filterSchedules();
            },
            error: (err) => {
                const msg = this.extractErrorMessage(err, 'Erro ao atualizar compromisso.');
                this.showToast(msg, 'error');
            }
        });
    }

    deleteSchedule(id: number): void {
        this.pendingDeleteId = id;
    }

    confirmDelete(): void {
        if (!this.pendingDeleteId) return;
        const idToDelete = this.pendingDeleteId;
        this.pendingDeleteId = null;

        this.scheduleService.deleteSchedule(idToDelete).subscribe({
            next: () => {
                this.showToast('Compromisso removido com sucesso!', 'success');
                this.loadSchedules();
            },
            error: (err) => {
                const msg = this.extractErrorMessage(err, 'Erro ao remover compromisso.');
                this.showToast(msg, 'error');
            }
        });
    }

    cancelDelete(): void {
        this.pendingDeleteId = null;
    }

    setMode(mode: 'MONTH' | 'DAY'): void {
        this.viewMode = mode;
        this.filterSchedules();
    }
}