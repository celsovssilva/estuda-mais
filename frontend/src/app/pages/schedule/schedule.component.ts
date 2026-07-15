import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ScheduleService } from '../../core/services/schedule/schedule.service';
import { ScheduleRequest, ScheduleResponse } from '../../core/models/schedule.models';
import { NavbarComponent } from "../../app/shared/navbar/navbar.component";

type DayStatus = 'none' | 'red' | 'orange' | 'green';

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
        endTime: ''
    };

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

    // Status calculado do dia atualmente selecionado (usado pra mensagem de aviso)
    selectedDayStatus: DayStatus = 'none';

    ngOnInit(): void {
        const today = new Date();
        this.currentYear = today.getFullYear();
        this.selectedDateStr = today.toISOString().split('T')[0];
        this.formSchedule.targetDate = this.selectedDateStr;
        this.loadSchedules();
    }
    loadSchedules(): void {
        this.scheduleService.getSchedulesByUser().subscribe({
            next: (res: ScheduleResponse[]) => {
                this.allSchedules = res || [];
                this.buildMonthlyCalendar();
                this.filterSchedules();
            },
            error: (err) => console.error('Erro ao buscar compromissos:', err)
        });
    }
    // Calcula o status (cor) de um conjunto de compromissos de um dia
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
        } else {
            this.selectedDayStatus = 'none';
        }

        this.filteredSchedules = dayItems.filter(s => !s.completed);
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

    addSchedule(): void {
        if (!this.formSchedule.title.trim() || !this.formSchedule.targetDate) {
            alert('Por favor, defina um Título e uma Data de Agendamento.');
            return;
        }

        const payload: ScheduleRequest = {
            title: this.formSchedule.title.trim(),
            description: this.formSchedule.description.trim() || 'Sem descrição cadastrada.',
            targetDate: this.formSchedule.targetDate,
            type: this.formSchedule.type,
            startTime: this.formSchedule.startTime ? this.formSchedule.startTime : null,
            endTime: this.formSchedule.endTime ? this.formSchedule.endTime : null
        };

        this.scheduleService.createSchedule(payload).subscribe({
            next: () => {
                this.formSchedule.title = '';
                this.formSchedule.description = '';
                this.formSchedule.startTime = '';
                this.formSchedule.endTime = '';
                this.loadSchedules();
            },
            error: (err) => {
                console.error('Erro na criação:', err);
                alert('Ocorreu uma falha ao registrar o compromisso.');
            }
        });
    }

    toggleSchedule(schedule: ScheduleResponse): void {
        this.scheduleService.toggleSchedule(schedule.id).subscribe({
            next: (updated: ScheduleResponse) => {
                // Atualiza o item dentro de allSchedules (precisa continuar lá pra colorir o dia)
                const idx = this.allSchedules.findIndex(s => s.id === updated.id);
                if (idx !== -1) {
                    this.allSchedules[idx] = updated;
                }
                this.buildMonthlyCalendar();
                this.filterSchedules();
            },
            error: (err) => console.error('Erro ao atualizar compromisso:', err)
        });
    }

    deleteSchedule(id: number): void {
        if (confirm('Remover definitivamente este compromisso de sua grade?')) {
            this.scheduleService.deleteSchedule(id).subscribe({
                next: () => this.loadSchedules(),
                error: (err) => console.error(err)
            });
        }
    }

    setMode(mode: 'MONTH' | 'DAY'): void {
        this.viewMode = mode;
        this.filterSchedules();
    }
}