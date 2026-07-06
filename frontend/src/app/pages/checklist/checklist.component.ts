import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChecklistService } from '../../core/services/checklist/checklist.service';

@Component({
    selector: 'app-checklist',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './checklist.component.html',
    styleUrls: ['./checklist.component.css']
})
export class ChecklistComponent implements OnInit {
    private checklistService = inject(ChecklistService);

    tasks: any[] = [];
    newTaskDescription: string = '';
    newTaskDate: string = '';

    ngOnInit(): void {
        this.loadTasks();
    }

    loadTasks(): void {
        this.checklistService.getTasksByUser().subscribe({
            next: (res: any) => {
                this.tasks = Array.isArray(res) ? res : [];
            },
            error: (err) => console.error('Erro ao carregar tarefas:', err)
        });
    }

    addTask(): void {
        if (!this.newTaskDescription.trim()) return;

        const payload = {
            description: this.newTaskDescription,
            executionDate: this.newTaskDate || '',
            completed: false
        };

        this.checklistService.createTask(payload).subscribe({
            next: () => {
                this.newTaskDescription = '';
                this.loadTasks();
            },
            error: (err) => console.error('Erro ao adicionar tarefa:', err)
        });
    }

    toggleTask(task: any): void {
        const originalState = !task.completed;

        this.checklistService.toggleTask(task.id).subscribe({
            next: () => console.log('Estado atualizado no servidor.'),
            error: (err) => {
                console.error(err);
                task.completed = originalState; // Reverte se der erro
            }
        });
    }

    deleteTask(id: number): void {
        this.checklistService.deleteTask(id).subscribe({
            next: () => this.loadTasks(),
            error: (err) => console.error('Erro ao deletar:', err)
        });
    }
}