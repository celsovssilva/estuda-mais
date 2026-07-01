import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ChecklistService } from '../../core/services/checklist/checklist.service';
import { AuthService } from '../../core/services/auth/auth.services';
import { ChecklistTaskResponse, ChecklistTaskRequest } from '../../core/models/checklist.models';

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
    private router = inject(Router);

    tasks: ChecklistTaskResponse[] = [];

    // Objeto para o formulário de nova tarefa
    newTask: ChecklistTaskRequest = {
        description: '',
        executionDate: new Date().toISOString().split('T')[0] // Data de hoje padrão
    };

    ngOnInit(): void {
        this.loadTasks();
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
                this.newTask.description = ''; // Limpa campo
            },
            error: (err) => alert('Erro ao criar tarefa')
        });
    }

    toggleTask(task: ChecklistTaskResponse): void {
        this.checklistService.toggleTask(task.id).subscribe({
            next: () => {
                task.completed = !task.completed;
            },
            error: (err) => console.error('Erro ao atualizar status', err)
        });
    }

    deleteTask(id: number): void {
        if (confirm('Deseja excluir esta tarefa?')) {
            this.checklistService.deleteTask(id).subscribe({
                next: () => {
                    this.tasks = this.tasks.filter(t => t.id !== id);
                },
                error: (err) => console.error('Erro ao deletar', err)
            });
        }
    }

    onLogout(): void {
        this.authService.logout();
        this.router.navigate(['/login']);
    }
}