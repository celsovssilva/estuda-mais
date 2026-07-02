import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

// Serviços do Sistema
import { ChecklistService } from '../../core/services/checklist/checklist.service';
import { AuthService } from '../../core/services/auth/auth.services';
import { NoteService } from '../../core/services/note/note.service';
import { ScheduleService } from '../../core/services/schedule/schedule.service';
import { StudyService } from '../../core/services/study/study.service';

// Modelos de Dados
import { ChecklistTaskResponse, ChecklistTaskRequest } from '../../core/models/checklist.models';
import { Note } from '../../core/models/note.models';
import { ScheduleRequest, ScheduleResponse } from '../../core/models/schedule.models';
import { GoalRequest, GoalResponse, StudySessionRequest, StudySessionResponse } from '../../core/models/study.models';

@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [CommonModule, FormsModule],
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


    tasks: ChecklistTaskResponse[] = [];
    newTask: ChecklistTaskRequest = {
        description: '',
        executionDate: new Date().toISOString().split('T')[0]
    };


    notes: Note[] = [];
    newNote = { title: '', content: '' };
    isEditingNote = false;
    editingNoteId: number | null = null;


    schedules: ScheduleResponse[] = [];
    newSchedule: ScheduleRequest = {
        title: '',
        description: '',
        targetDate: new Date().toISOString().split('T')[0],
        type: 'DAY'
    };
    isEditingSchedule = false;
    editingScheduleId: number | null = null;


    goals: GoalResponse[] = [];
    sessions: StudySessionResponse[] = [];
    dashboardMetrics: any = null;

    newGoal: GoalRequest = {
        category: '',
        targetMinutesPerDay: 30
    };

    newSession: StudySessionRequest = {
        subject: '',
        durationMinutes: 45
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


    submitSession(): void {
        if (!this.newSession.subject || this.newSession.durationMinutes < 1) return;

        this.studyService.registerSession(this.newSession).subscribe({
            next: (res) => {
                this.sessions.unshift(res);
                this.latestFeedback = res.feedbackMessage;

                this.newSession.subject = '';
                this.newSession.durationMinutes = 45;

                this.loadDashboardMetrics();
                setTimeout(() => this.latestFeedback = null, 8000);
            },
            error: (err) => {
                alert('Erro ao registrar sessão de estudos.');
                console.error(err);
            }
        });
    }


    loadTasks(): void {
        this.checklistService.getTasksByUser().subscribe({
            next: (res) => this.tasks = res,
            error: (err) => console.error('Erro ao carregar tarefas', err)
        });
    }

    addTask(): void {
        if (!this.newTask.description) return;
        this.checklistService.createTask(this.newTask).subscribe({
            next: (res) => {
                this.tasks.push(res);
                this.newTask.description = '';
            },
            error: (err) => alert('Erro ao criar tarefa')
        });
    }

    toggleTask(task: ChecklistTaskResponse): void {
        this.checklistService.toggleTask(task.id).subscribe({
            next: () => { task.completed = !task.completed; },
            error: (err) => console.error('Erro ao atualizar status', err)
        });
    }

    deleteTask(id: number): void {
        if (confirm('Deseja excluir esta tarefa?')) {
            this.checklistService.deleteTask(id).subscribe({
                next: () => { this.tasks = this.tasks.filter(t => t.id !== id); },
                error: (err) => console.error('Erro ao deletar tarefa', err)
            });
        }
    }


    loadNotes(): void {
        this.noteService.getNotesByUser().subscribe({
            next: (res) => this.notes = res,
            error: (err) => console.error('Erro ao carregar notas', err)
        });
    }

    saveNote(): void {
        if (!this.newNote.title || !this.newNote.content) return;

        const noteData: Note = {
            title: this.newNote.title,
            content: this.newNote.content,
            referenceDate: new Date().toISOString().split('T')[0]
        };

        if (this.isEditingNote && this.editingNoteId !== null) {
            this.noteService.updateNote(this.editingNoteId, noteData).subscribe({
                next: (res) => {
                    this.notes = this.notes.map(n => n.id === this.editingNoteId ? res : n);
                    this.resetNoteForm();
                },
                error: (err) => alert('Erro ao atualizar nota')
            });
        } else {
            this.noteService.createNote(noteData).subscribe({
                next: (res) => {
                    this.notes.push(res);
                    this.resetNoteForm();
                },
                error: (err) => alert('Erro ao criar nota')
            });
        }
    }

    prepareEditNote(note: Note): void {
        if (!note.id) return;
        this.isEditingNote = true;
        this.editingNoteId = note.id;
        this.newNote.title = note.title;
        this.newNote.content = note.content;
    }

    cancelEditNote(): void { this.resetNoteForm(); }

    resetNoteForm(): void {
        this.isEditingNote = false;
        this.editingNoteId = null;
        this.newNote.title = '';
        this.newNote.content = '';
    }

    deleteNote(id: number | undefined): void {
        if (id === undefined) return;
        if (confirm('Deseja excluir esta nota?')) {
            this.noteService.deleteNote(id).subscribe({
                next: () => {
                    this.notes = this.notes.filter(n => n.id !== id);
                    if (this.editingNoteId === id) this.resetNoteForm();
                },
                error: (err) => console.error('Erro ao deletar nota', err)
            });
        }
    }

    loadSchedules(): void {
        this.scheduleService.getSchedulesByUser().subscribe({
            next: (res) => this.schedules = res,
            error: (err) => console.error('Erro ao carregar cronograma', err)
        });
    }

    saveSchedule(): void {
        if (!this.newSchedule.title || !this.newSchedule.targetDate) return;

        if (this.isEditingSchedule && this.editingScheduleId !== null) {
            this.scheduleService.updateSchedule(this.editingScheduleId, this.newSchedule).subscribe({
                next: (res) => {
                    this.schedules = this.schedules.map(s => s.id === this.editingScheduleId ? res : s);
                    this.resetScheduleForm();
                },
                error: (err) => alert('Erro ao atualizar cronograma')
            });
        } else {
            this.scheduleService.createSchedule(this.newSchedule).subscribe({
                next: (res) => {
                    this.schedules.push(res);
                    this.resetScheduleForm();
                },
                error: (err) => alert('Erro ao criar cronograma')
            });
        }
    }

    prepareEditSchedule(schedule: ScheduleResponse): void {
        if (!schedule.id) return;
        this.isEditingSchedule = true;
        this.editingScheduleId = schedule.id;
        this.newSchedule = {
            title: schedule.title,
            description: schedule.description,
            targetDate: schedule.targetDate,
            type: schedule.type
        };
    }

    cancelEditSchedule(): void { this.resetScheduleForm(); }

    resetScheduleForm(): void {
        this.isEditingSchedule = false;
        this.editingScheduleId = null;
        this.newSchedule = {
            title: '',
            description: '',
            targetDate: new Date().toISOString().split('T')[0],
            type: 'DAY'
        };
    }

    deleteSchedule(id: number | undefined): void {
        if (id === undefined) return;
        if (confirm('Deseja excluir este evento?')) {
            this.scheduleService.deleteSchedule(id).subscribe({
                next: () => {
                    this.schedules = this.schedules.filter(s => s.id !== id);
                    if (this.editingScheduleId === id) this.resetScheduleForm();
                },
                error: (err) => console.error('Erro ao deletar evento', err)
            });
        }
    }

    loadGoals(): void {
        this.studyService.getGoals().subscribe({
            next: (res) => this.goals = res,
            error: (err) => console.error('Erro ao buscar metas', err)
        });
    }

    loadSessionHistory(): void {
        this.studyService.getSessionHistory().subscribe({
            next: (res) => this.sessions = res,
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
        if (!this.newGoal.category || this.newGoal.targetMinutesPerDay < 1) return;
        this.studyService.saveGoal(this.newGoal).subscribe({
            next: (res) => {
                this.goals.push(res);
                this.newGoal.category = '';
                this.loadDashboardMetrics();
            },
            error: (err) => alert('Erro ao salvar meta.')
        });
    }

    onLogout(): void {
        this.authService.logout();
        this.router.navigate(['/login']);
    }
}