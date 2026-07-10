import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NoteService } from '../../core/services/note/note.service'; // Ajuste o caminho se necessário
import { Note } from '../../core/models/note.models';
import {NavbarComponent} from "../../app/shared/navbar/navbar.component";

@Component({
    selector: 'app-notes',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule,NavbarComponent],
    templateUrl: './notes.component.html',
    styleUrls: ['./notes.component.css']
})
export class NotesComponent implements OnInit {
    private noteService = inject(NoteService);

    // Listagem de notas vindas do backend
    allNotes: Note[] = [];

    // Objeto reativo para o formulário (Editor)
    formNote: Note = {
        title: '',
        content: '',
        referenceDate: new Date().toISOString().split('T')[0] // Data de hoje como padrão
    };

    // Controle de estado de edição
    isEditing: boolean = false;

    ngOnInit(): void {
        this.loadNotes();
    }

    loadNotes(): void {
        this.noteService.getNotesByUser().subscribe({
            next: (res: Note[]) => {
                this.allNotes = res || [];
            },
            error: (err) => console.error('Erro ao carregar notas:', err)
        });
    }

    // Salva ou atualiza a nota dependendo do estado atual
    saveNote(): void {
        if (!this.formNote.title.trim() || !this.formNote.content.trim()) {
            alert('Por favor, preencha o título e o conteúdo da anotação.');
            return;
        }

        if (this.isEditing && this.formNote.id) {
            // Modo Atualização
            this.noteService.updateNote(this.formNote.id, this.formNote).subscribe({
                next: () => {
                    this.clearForm();
                    this.loadNotes();
                },
                error: (err) => console.error('Erro ao atualizar nota:', err)
            });
        } else {
            // Modo Criação
            this.noteService.createNote(this.formNote).subscribe({
                next: () => {
                    this.clearForm();
                    this.loadNotes();
                },
                error: (err) => console.error('Erro ao criar nota:', err)
            });
        }
    }

    // Prepara o formulário para edição ao clicar em uma nota da lista
    prepareEdit(note: Note): void {
        this.isEditing = true;
        this.formNote = { ...note }; // Clona o objeto para não alterar a lista antes de salvar
    }

    deleteNote(id: number | undefined, event: Event): void {
        event.stopPropagation(); // Evita que o clique abra o modo de edição do card
        if (!id) return;

        if (confirm('Deseja excluir permanentemente esta anotação?')) {
            this.noteService.deleteNote(id).subscribe({
                next: () => {
                    // Se a nota excluída estava sendo editada no momento, limpa o formulário
                    if (this.formNote.id === id) {
                        this.clearForm();
                    }
                    this.loadNotes();
                },
                error: (err) => console.error('Erro ao deletar nota:', err)
            });
        }
    }

    clearForm(): void {
        this.isEditing = false;
        this.formNote = {
            title: '',
            content: '',
            referenceDate: new Date().toISOString().split('T')[0]
        };
    }
}