import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChecklistService } from '../../core/services/checklist/checklist.service';
import { NavbarComponent } from "../../app/shared/navbar/navbar.component";
import { ChecklistTaskRequest, ChecklistTaskResponse } from '../../core/models/checklist.models'; // ajuste o caminho conforme o seu projeto

@Component({
    selector: 'app-checklist',
    standalone: true,
    imports: [CommonModule, FormsModule, NavbarComponent],
    templateUrl: './checklist.component.html',
    styleUrls: ['./checklist.component.css']
})
export class ChecklistComponent implements OnInit {
    private checklistService = inject(ChecklistService);

    tasks: ChecklistTaskResponse[] = [];
    newTaskDescription: string = '';
    newTaskDate: string = '';

    ngOnInit(): void {
        this.loadTasks();
    }

    loadTasks(): void {
        this.checklistService.getTasksByUser().subscribe({
            next: (res: ChecklistTaskResponse[]) => {
                const all = Array.isArray(res) ? res : [];
                this.tasks = all.filter(t => !t.completed);
            },
            error: (err) => console.error('Erro ao carregar tarefas:', err)
        });
    }

    addTask(): void {
        if (!this.newTaskDescription.trim()) return;

        const payload: ChecklistTaskRequest = {
            description: this.newTaskDescription,
            executionDate: this.newTaskDate || ''
        };

        this.checklistService.createTask(payload).subscribe({
            next: () => {
                this.newTaskDescription = '';
                this.newTaskDate = '';
                this.loadTasks();
            },
            error: (err) => console.error('Erro ao adicionar tarefa:', err)
        });
    }

    toggleTask(task: ChecklistTaskResponse): void {
        this.checklistService.toggleTask(task.id).subscribe({
            next: () => {
                this.tasks = this.tasks.filter(t => t.id !== task.id);
            },
            error: (err) => console.error('Erro ao atualizar tarefa:', err)
        });
    }

    deleteTask(id: number): void {
        this.checklistService.deleteTask(id).subscribe({
            next: () => this.loadTasks(),
            error: (err) => console.error('Erro ao deletar:', err)
        });
    }
}