import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

// Imports do FullCalendar
import { FullCalendarModule } from '@fullcalendar/angular';
import { CalendarOptions, EventInput } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import listPlugin from '@fullcalendar/list';
import interactionPlugin from '@fullcalendar/interaction';

// Serviços do Sistema
import { ChecklistService } from '../../core/services/checklist/checklist.service';
import { AuthService } from '../../core/services/auth/auth.services';
import { NoteService } from '../../core/services/note/note.service';
import { ScheduleService } from '../../core/services/schedule/schedule.service';
import { StudyService } from '../../core/services/study/study.service';

@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [CommonModule, FormsModule, FullCalendarModule],
    templateUrl: './dashboard.component.html',
    styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
    private checklistService = inject(ChecklistService);
    private authService = inject(AuthService);
    private noteService = inject(NoteService);
    private scheduleService = inject(ScheduleService);
    private studyService = inject(StudyService);
    private router = inject(Router);


    tasks: any[] = [];
    newTask: any = {
        description: '',
        descricao: '',
        executionDate: new Date().toISOString().split('T')[0],
        dataExecucao: new Date().toISOString().split('T')[0]
    };

    notes: any[] = [];
    newNote = { title: '', titulo: '', content: '', conteudo: '' };
    isEditingNote = false;
    editingNoteId: number | null = null;


    schedules: any[] = [];
    newSchedule: any = {
        title: '',
        titulo: '',
        description: '',
        descricao: '',
        targetDate: new Date().toISOString().split('T')[0],
        dataAlvo: new Date().toISOString().split('T')[0],
        type: 'DAY',
        tipo: 'DAY'
    };
    isEditingSchedule = false;
    editingScheduleId: number | null = null;

    // Configurações do Calendário
    calendarOptions: CalendarOptions = {
        initialView: 'dayGridMonth',
        plugins: [dayGridPlugin, timeGridPlugin, listPlugin, interactionPlugin],
        locale: 'pt-br',
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,listWeek'
        },
        buttonText: {
            today: 'Hoje',
            month: 'Mês',
            week: 'Semana',
            day: 'Dia',
            list: 'Agenda'
        },
        events: [],
        eventClick: (info) => this.handleCalendarEventClick(info.event)
    };

    // Variáveis de Controle do Pop-up Diário
    showDailyPopup = false;
    todayEvents: any[] = [];
    todayTasks: any[] = [];


    goals: any[] = [];
    sessions: any[] = [];
    dashboardMetrics: any = null;

    newGoal: any = {
        category: '',
        categoria: '',
        targetMinutesPerDay: 30,
        minutosPorDia: 30
    };

    newSession: any = {
        subject: '',
        materia: '',
        durationMinutes: 45,
        duracaoMinutos: 45
    };

    latestFeedback: string | null = null;

    ngOnInit(): void {
        this.loadTasks();
        this.loadNotes();
        this.loadSchedules();
        this.loadGoals();
        this.loadSessionHistory();
        this.loadDashboardMetrics();
    }


    private extractArray(res: any): any[] {
        if (!res) return [];
        if (Array.isArray(res)) return res;
        if (Array.isArray(res.content)) return res.content; // Spring Pageable
        if (Array.isArray(res.data)) return res.data;       // Response Wrappers
        return [];
    }

    checkDailyPopup(): void {
        const todayStr = new Date().toISOString().split('T')[0];
        const lastSeen = localStorage.getItem('lastDailyPopupSeen');

        this.todayEvents = this.schedules.filter(s => {
            const date = s.targetDate || s.dataAlvo;
            return date === todayStr;
        });

        this.todayTasks = this.tasks.filter(t => {
            const date = t.executionDate || t.dataExecucao;
            return date === todayStr && !t.completed;
        });

        if ((this.todayEvents.length > 0 || this.todayTasks.length > 0) && lastSeen !== todayStr) {
            this.showDailyPopup = true;
        }
    }

    closePopup(): void {
        this.showDailyPopup = false;
        localStorage.setItem('lastDailyPopupSeen', new Date().toISOString().split('T')[0]);
    }


    updateCalendarEvents(): void {
        const mappedEvents: EventInput[] = this.schedules.map(s => {
            const title = s.title || s.titulo || 'Sem título';
            const date = s.targetDate || s.dataAlvo || s.executionDate || s.dataExecucao;
            const desc = s.description || s.descricao || '';
            const type = s.type || s.tipo || 'DAY';

            return {
                id: s.id?.toString(),
                title: title,
                start: date,
                description: desc,
                allDay: true,
                color: type === 'DAY' ? '#3b82f6' : type === 'WEEK' ? '#10b981' : type === 'MONTH' ? '#8b5cf6' : '#ec4899'
            };
        });
        this.calendarOptions.events = mappedEvents;
    }

    handleCalendarEventClick(calendarEvent: any): void {
        const schedule = this.schedules.find(s => s.id?.toString() === calendarEvent.id);
        if (schedule) {
            this.prepareEditSchedule(schedule);
        }
    }


    submitSession(): void {
        const subjectValue = this.newSession.subject || this.newSession.materia;
        const durationValue = this.newSession.durationMinutes || this.newSession.duracaoMinutos;
        if (!subjectValue || durationValue < 1) return;

        const payload = {
            subject: subjectValue, materia: subjectValue,
            durationMinutes: durationValue, duracaoMinutos: durationValue
        };

        this.studyService.registerSession(payload).subscribe({
            next: (res: any) => {
                this.sessions.unshift(res);
                this.latestFeedback = res.feedbackMessage || res.mensagemFeedback || '';

                this.newSession.subject = '';
                this.newSession.materia = '';
                this.newSession.durationMinutes = 45;
                this.newSession.duracaoMinutos = 45;

                this.loadDashboardMetrics();
                setTimeout(() => this.latestFeedback = null, 8000);
            },
            error: (err) => console.error('Erro ao registrar sessão', err)
        });
    }


    loadTasks(): void {
        this.checklistService.getTasksByUser().subscribe({
            next: (res) => {
                this.tasks = this.extractArray(res);
                this.checkDailyPopup();
            },
            error: (err) => console.error('Erro ao carregar tarefas', err)
        });
    }

    addTask(): void {
        const descValue = this.newTask.description || this.newTask.descricao;
        const dateValue = this.newTask.executionDate || this.newTask.dataExecucao;
        if (!descValue) return;

        const payload = {
            description: descValue, descricao: descValue,
            executionDate: dateValue, dataExecucao: dateValue
        };

        this.checklistService.createTask(payload).subscribe({
            next: (res) => {
                this.tasks.push(res);
                this.newTask.description = '';
                this.newTask.descricao = '';
                this.checkDailyPopup();
            },
            error: (err) => console.error('Erro ao adicionar tarefa', err)
        });
    }

    toggleTask(task: any): void {
        this.checklistService.toggleTask(task.id).subscribe({
            next: () => {
                task.completed = !task.completed;
                this.checkDailyPopup();
            },
            error: (err) => console.error(err)
        });
    }

    deleteTask(id: number): void {
        if (confirm('Deseja excluir esta tarefa?')) {
            this.checklistService.deleteTask(id).subscribe({
                next: () => {
                    this.tasks = this.tasks.filter(t => t.id !== id);
                    this.checkDailyPopup();
                },
                error: (err) => console.error(err)
            });
        }
    }


    loadNotes(): void {
        this.noteService.getNotesByUser().subscribe({
            next: (res) => {
                this.notes = this.extractArray(res);
            },
            error: (err) => console.error('Erro ao carregar notas', err)
        });
    }

    saveNote(): void {
        const titleValue = this.newNote.title || this.newNote.titulo;
        const contentValue = this.newNote.content || this.newNote.conteudo;
        if (!titleValue || !contentValue) return;

        const noteData: any = {
            title: titleValue, titulo: titleValue,
            content: contentValue, conteudo: contentValue,
            referenceDate: new Date().toISOString().split('T')[0],
            dataReferencia: new Date().toISOString().split('T')[0]
        };

        if (this.isEditingNote && this.editingNoteId !== null) {
            this.noteService.updateNote(this.editingNoteId, noteData).subscribe({
                next: (res) => {
                    this.notes = this.notes.map(n => n.id === this.editingNoteId ? res : n);
                    this.resetNoteForm();
                },
                error: (err) => console.error(err)
            });
        } else {
            this.noteService.createNote(noteData).subscribe({
                next: (res) => {
                    this.notes.push(res);
                    this.resetNoteForm();
                },
                error: (err) => console.error(err)
            });
        }
    }

    prepareEditNote(note: any): void {
        if (!note.id) return;
        this.isEditingNote = true;
        this.editingNoteId = note.id;
        this.newNote.title = note.title || note.titulo;
        this.newNote.titulo = note.title || note.titulo;
        this.newNote.content = note.content || note.conteudo;
        this.newNote.conteudo = note.content || note.conteudo;
    }

    cancelEditNote(): void { this.resetNoteForm(); }

    resetNoteForm(): void {
        this.isEditingNote = false;
        this.editingNoteId = null;
        this.newNote.title = ''; this.newNote.titulo = '';
        this.newNote.content = ''; this.newNote.conteudo = '';
    }

    deleteNote(id: number | undefined): void {
        if (id === undefined) return;
        if (confirm('Deseja excluir esta nota?')) {
            this.noteService.deleteNote(id).subscribe({
                next: () => {
                    this.notes = this.notes.filter(n => n.id !== id);
                    if (this.editingNoteId === id) this.resetNoteForm();
                },
                error: (err) => console.error(err)
            });
        }
    }


    loadSchedules(): void {
        this.scheduleService.getSchedulesByUser().subscribe({
            next: (res) => {
                this.schedules = this.extractArray(res);
                this.updateCalendarEvents();
                this.checkDailyPopup();
            },
            error: (err) => console.error('Erro ao carregar cronograma', err)
        });
    }

    saveSchedule(): void {
        const titleValue = this.newSchedule.title || this.newSchedule.titulo;
        const descValue = this.newSchedule.description || this.newSchedule.descricao;
        const dateValue = this.newSchedule.targetDate || this.newSchedule.dataAlvo;
        const typeValue = this.newSchedule.type || this.newSchedule.tipo;
        if (!titleValue || !dateValue) return;

        const payload = {
            title: titleValue, titulo: titleValue,
            description: descValue, descricao: descValue,
            targetDate: dateValue, dataAlvo: dateValue,
            type: typeValue, tipo: typeValue
        };

        if (this.isEditingSchedule && this.editingScheduleId !== null) {
            this.scheduleService.updateSchedule(this.editingScheduleId, payload).subscribe({
                next: (res) => {
                    this.schedules = this.schedules.map(s => s.id === this.editingScheduleId ? res : s);
                    this.updateCalendarEvents();
                    this.resetScheduleForm();
                    this.checkDailyPopup();
                },
                error: (err) => console.error(err)
            });
        } else {
            this.scheduleService.createSchedule(payload).subscribe({
                next: (res) => {
                    this.schedules.push(res);
                    this.updateCalendarEvents();
                    this.resetScheduleForm();
                    this.checkDailyPopup();
                },
                error: (err) => console.error(err)
            });
        }
    }

    prepareEditSchedule(schedule: any): void {
        if (!schedule.id) return;
        this.isEditingSchedule = true;
        this.editingScheduleId = schedule.id;
        this.newSchedule = {
            title: schedule.title || schedule.titulo,
            titulo: schedule.title || schedule.titulo,
            description: schedule.description || schedule.descricao,
            descricao: schedule.description || schedule.descricao,
            targetDate: schedule.targetDate || schedule.dataAlvo,
            dataAlvo: schedule.targetDate || schedule.dataAlvo,
            type: schedule.type || schedule.tipo,
            tipo: schedule.type || schedule.tipo
        };
    }

    cancelEditSchedule(): void { this.resetScheduleForm(); }

    resetScheduleForm(): void {
        this.isEditingSchedule = false;
        this.editingScheduleId = null;
        this.newSchedule = {
            title: '', titulo: '',
            description: '', descricao: '',
            targetDate: new Date().toISOString().split('T')[0],
            dataAlvo: new Date().toISOString().split('T')[0],
            type: 'DAY', tipo: 'DAY'
        };
    }

    deleteSchedule(id: number | undefined): void {
        if (id === undefined) return;
        if (confirm('Deseja excluir este evento?')) {
            this.scheduleService.deleteSchedule(id).subscribe({
                next: () => {
                    this.schedules = this.schedules.filter(s => s.id !== id);
                    this.updateCalendarEvents();
                    if (this.editingScheduleId === id) this.resetScheduleForm();
                    this.checkDailyPopup();
                },
                error: (err) => console.error(err)
            });
        }
    }


    loadGoals(): void {
        this.studyService.getGoals().subscribe({
            next: (res) => {
                this.goals = this.extractArray(res);
            },
            error: (err) => console.error('Erro ao buscar metas', err)
        });
    }

    loadSessionHistory(): void {
        this.studyService.getSessionHistory().subscribe({
            next: (res) => {
                this.sessions = this.extractArray(res);
            },
            error: (err) => console.error('Erro ao buscar histórico', err)
        });
    }

    loadDashboardMetrics(): void {
        this.studyService.getStudyDashboard().subscribe({
            next: (res) => this.dashboardMetrics = res,
            error: (err) => console.error('Erro ao carregar métricas', err)
        });
    }

    submitGoal(): void {
        const catValue = this.newGoal.category || this.newGoal.categoria;
        const minValue = this.newGoal.targetMinutesPerDay || this.newGoal.minutosPorDia;
        if (!catValue || minValue < 1) return;

        const payload = {
            category: catValue, categoria: catValue,
            targetMinutesPerDay: minValue, minutosPorDia: minValue
        };

        this.studyService.saveGoal(payload).subscribe({
            next: (res) => {
                this.goals.push(res);
                this.newGoal.category = '';
                this.newGoal.categoria = '';
                this.loadDashboardMetrics();
            },
            error: (err) => console.error(err)
        });
    }

    onLogout(): void {
        this.authService.logout();
        this.router.navigate(['/login']);
    }
}