import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ScheduleService } from '../../core/services/schedule/schedule.service';
import { ScheduleRequest, ScheduleResponse } from '../../core/models/schedule.models';

@Component({
    selector: 'app-agenda',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule],
    templateUrl: './schedule.component.html',
    styleUrls: ['./schedule.component.css']
})
export class AgendaComponent implements OnInit {
    private scheduleService = inject(ScheduleService);

    // Armazenamento de dados tipados
    allSchedules: ScheduleResponse[] = [];
    filteredSchedules: ScheduleResponse[] = [];

    // Objeto reativo do formulário baseado em ScheduleRequest
    formSchedule = {
        title: '',
        description: '',
        targetDate: '',
        type: 'DAY', // Padrão inicial
        startTime: '',
        endTime: ''
    };

    // Controle de estados do Calendário Visual
    currentYear: number = 2026; // Alinhado com o ano corrente do sistema
    currentMonth: number = new Date().getMonth(); // 0 a 11
    selectedDateStr: string = ''; // Data ativa selecionada no formato YYYY-MM-DD
    viewMode: 'MONTH' | 'DAY' = 'MONTH';

    // Auxiliares estruturais
    calendarWeeks: any[][] = [];
    weekDays: string[] = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'];
    monthNames: string[] = [
        'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
        'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
    ];

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

    // Monta a matriz de dias (grade do calendário tradicional)
    buildMonthlyCalendar(): void {
        const firstDayIndex = new Date(this.currentYear, this.currentMonth, 1).getDay();
        const totalDays = new Date(this.currentYear, this.currentMonth + 1, 0).getDate();

        const daysArray: any[] = [];

        // Preenche lacunas do mês anterior
        for (let i = 0; i < firstDayIndex; i++) {
            daysArray.push(null);
        }

        // Preenche os dias reais do mês correspondente
        for (let day = 1; day <= totalDays; day++) {
            const dayString = String(day).padStart(2, '0');
            const monthString = String(this.currentMonth + 1).padStart(2, '0');
            const dateStr = `${this.currentYear}-${monthString}-${dayString}`;

            // Vincula compromissos específicos deste dia para renderizar indicadores visuais
            const daySchedules = this.allSchedules.filter(s => s.targetDate === dateStr);

            daysArray.push({
                day,
                dateStr,
                hasEvents: daySchedules.length > 0,
                eventsCount: daySchedules.length
            });
        }

        // Fatia o array plano em blocos semanais de 7 dias
        this.calendarWeeks = [];
        while (daysArray.length > 0) {
            this.calendarWeeks.push(daysArray.splice(0, 7));
        }
    }

    filterSchedules(): void {
        if (this.viewMode === 'MONTH') {
            this.filteredSchedules = this.allSchedules.filter(s => {
                if (!s.targetDate) return false;
                const d = new Date(s.targetDate + 'T00:00:00');
                return d.getFullYear() === this.currentYear && d.getMonth() === this.currentMonth;
            });
        } else {
            this.filteredSchedules = this.allSchedules.filter(s => s.targetDate === this.selectedDateStr);
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

    addSchedule(): void {
        if (!this.formSchedule.title.trim() || !this.formSchedule.targetDate) {
            alert('Por favor, defina um Título e uma Data de Agendamento.');
            return;
        }

        // Montando o payload mapeado estritamente com a ScheduleRequest
        const payload: ScheduleRequest = {
            title: this.formSchedule.title.trim(),
            description: this.formSchedule.description.trim() || 'Sem descrição cadastrada.',
            targetDate: this.formSchedule.targetDate,
            type: this.formSchedule.type,
            // Passando nulo ou string limpa respeitando o tipo string | null do seu model
            startTime: this.formSchedule.startTime ? this.formSchedule.startTime : null,
            endTime: this.formSchedule.endTime ? this.formSchedule.endTime : null
        };

        this.scheduleService.createSchedule(payload).subscribe({
            next: () => {
                // Reseta os campos mantendo apenas a data selecionada
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