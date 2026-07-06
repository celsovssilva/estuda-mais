import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth/auth.services';
import { ChecklistService } from '../../core/services/checklist/checklist.service';
import { NoteService } from '../../core/services/note/note.service';
import { ScheduleService } from '../../core/services/schedule/schedule.service';
import { ScheduleRequest, ScheduleResponse } from '../../core/models/schedule.models';
import { Router } from '@angular/router';

@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
    private authService = inject(AuthService);
    private checklistService = inject(ChecklistService);
    private noteService = inject(NoteService);
    private scheduleService = inject(ScheduleService);
    private router = inject(Router);

    profileData = {
        name: '',
        email: '',
        password: ''
    };
    isSavingProfile = false;

    newTaskTitle = '';
    newTaskDate = '2026-07-03';

    newNoteTitle = '';
    newNoteContent = '';

    newScheduleTitle = '';
    newScheduleDescription = '';
    newScheduleDate = '2026-07-03';
    newScheduleType = 'DAY';
    newScheduleStartTime = '';
    newScheduleEndTime = '';

    tasks: any[] = [];
    notes: any[] = [];
    schedules: ScheduleResponse[] = [];

    metrics: any = {
        completedTasks: 0,
        pendingTasks: 0,
        totalSchedules: 0
    };

    ngOnInit(): void {
        this.loadTasks();
        this.loadNotes();
        this.loadSchedules();
        this.loadDashboardMetrics();

        const savedUser = localStorage.getItem('currentUser');
        if (savedUser) {
            const user = JSON.parse(savedUser);
            this.profileData.name = user.name || user.nome || '';
            this.profileData.email = user.email || '';
        }
    }

    loadTasks(): void {
        this.checklistService.getTasksByUser().subscribe({
            next: (res) => { this.tasks = Array.isArray(res) ? res : []; },
            error: (err) => { console.error(err); this.tasks = []; }
        });
    }

    loadNotes(): void {
        this.noteService.getNotesByUser().subscribe({
            next: (res) => { this.notes = Array.isArray(res) ? res : []; },
            error: (err) => { console.error(err); this.notes = []; }
        });
    }

    loadSchedules(): void {
        this.scheduleService.getSchedulesByUser().subscribe({
            next: (res: any) => { this.schedules = Array.isArray(res) ? res : []; },
            error: (err) => { console.error(err); this.schedules = []; }
        });
    }

    submitProfileUpdate(): void {
        const nameTrimmed = this.profileData.name?.trim();
        const emailTrimmed = this.profileData.email?.trim();
        const passwordTrimmed = this.profileData.password?.trim();

        if (!nameTrimmed || !emailTrimmed || !passwordTrimmed) {
            alert('Por favor, mantenha seu nome e e-mail e digite a nova senha.');
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
                alert('Perfil atualizado com sucesso! Por segurança, por favor faça login novamente com seus novos dados.');

                localStorage.clear();


                this.router.navigate(['/login']);
            },
            error: (err) => {
                this.isSavingProfile = false;
                console.error('Erro na requisição:', err);
                alert('Falha ao atualizar cadastro. Verifique os dados inseridos.');
            }
        });
    }

    addTask(): void {
        if (!this.newTaskTitle) return;
        this.checklistService.createTask({ description: this.newTaskTitle, targetDate: this.newTaskDate } as any).subscribe({
            next: () => {
                this.newTaskTitle = '';
                this.loadTasks();
                this.loadDashboardMetrics();
            },
            error: (err) => console.error(err)
        });
    }

    toggleTask(task: any): void {

        const originalState = !task.completed;


        if (task.completed) {
            this.metrics.completedTasks++;
            this.metrics.pendingTasks--;
        } else {
            this.metrics.completedTasks--;
            this.metrics.pendingTasks++;
        }


        this.checklistService.toggleTask(task.id).subscribe({
            next: () => {
                console.log('Tarefa atualizada no servidor com sucesso.');
            },
            error: (err) => {
                console.error('Erro ao atualizar tarefa:', err);
                alert('Não foi possível salvar o status da tarefa no servidor.');


                task.completed = originalState;
                if (task.completed) {
                    this.metrics.completedTasks++;
                    this.metrics.pendingTasks--;
                } else {
                    this.metrics.completedTasks--;
                    this.metrics.pendingTasks++;
                }
            }
        });
    }

    loadDashboardMetrics(): void {
        this.checklistService.getTasksByUser().subscribe({
            next: (res: any) => {
                const list = Array.isArray(res) ? res : [];
                // Ajustado de t.done para t.completed para ler os dados certos ao logar/carregar a página
                this.metrics.completedTasks = list.filter((t: any) => t.completed).length;
                this.metrics.pendingTasks = list.filter((t: any) => !t.completed).length;
            }
        });
        this.scheduleService.getSchedulesByUser().subscribe({
            next: (res: any) => {
                this.metrics.totalSchedules = Array.isArray(res) ? res.length : 0;
            }
        });
    }

    deleteTask(id: number): void {
        this.checklistService.deleteTask(id).subscribe({
            next: () => { this.loadTasks(); this.loadDashboardMetrics(); },
            error: (err) => console.error(err)
        });
    }

    addNote(): void {
        if (!this.newNoteTitle || !this.newNoteContent) return;
        this.noteService.createNote({ title: this.newNoteTitle, content: this.newNoteContent, referenceDate: new Date().toISOString().split('T')[0] } as any).subscribe({
            next: () => {
                this.newNoteTitle = '';
                this.newNoteContent = '';
                this.loadNotes();
            },
            error: (err) => console.error(err)
        });
    }

    deleteNote(id: number): void {
        this.noteService.deleteNote(id).subscribe({
            next: () => this.loadNotes(),
            error: (err) => console.error(err)
        });
    }

    addSchedule(): void {
        if (!this.newScheduleTitle) return;

        const scheduleRequest: ScheduleRequest = {
            title: this.newScheduleTitle,
            description: this.newScheduleDescription,
            targetDate: this.newScheduleDate,
            type: this.newScheduleType,
            startTime: this.newScheduleType === 'DAY' && this.newScheduleStartTime ? this.newScheduleStartTime : null,
            endTime: this.newScheduleType === 'DAY' && this.newScheduleEndTime ? this.newScheduleEndTime : null
        };

        this.scheduleService.createSchedule(scheduleRequest).subscribe({
            next: () => {
                this.newScheduleTitle = '';
                this.newScheduleDescription = '';
                this.newScheduleStartTime = '';
                this.newScheduleEndTime = '';
                this.loadSchedules();
                this.loadDashboardMetrics();
            },
            error: (err) => console.error(err)
        });
    }

    deleteSchedule(id: number): void {
        this.scheduleService.deleteSchedule(id).subscribe({
            next: () => { this.loadSchedules(); this.loadDashboardMetrics(); },
            error: (err) => console.error(err)
        });
    }
}