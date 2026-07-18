import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NoteService } from '../../core/services/note/note.service';
import { Note } from '../../core/models/note.models';
import { NavbarComponent } from "../../app/shared/navbar/navbar.component";

@Component({
    selector: 'app-notes',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule, NavbarComponent],
    templateUrl: './notes.component.html',
    styleUrls: ['./notes.component.css']
})
export class NotesComponent implements OnInit {
    private noteService = inject(NoteService);

    allNotes: Note[] = [];

    formNote: Note = {
        title: '',
        content: '',
        referenceDate: new Date().toISOString().split('T')[0]
    };

    selectedFile: File | null = null;
    removeCurrentAttachment: boolean = false;

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

    onFileSelected(event: Event): void {
        const input = event.target as HTMLInputElement;
        if (input.files && input.files.length > 0) {
            const file = input.files[0];
            if (file.size > 5 * 1024 * 1024) {
                alert('O arquivo deve ter no máximo 5MB.');
                input.value = '';
                return;
            }
            this.selectedFile = file;
            this.removeCurrentAttachment = false;
        }
    }

    removeAttachment(): void {
        this.selectedFile = null;
        this.removeCurrentAttachment = true;
    }

    saveNote(): void {
        if (!this.formNote.title.trim() || !this.formNote.content.trim()) {
            alert('Por favor, preencha o título e o conteúdo da anotação.');
            return;
        }

        if (this.isEditing && this.formNote.id) {
            this.noteService.updateNote(this.formNote.id, this.formNote, this.selectedFile, this.removeCurrentAttachment).subscribe({
                next: () => {
                    this.clearForm();
                    this.loadNotes();
                },
                error: (err) => console.error('Erro ao atualizar nota:', err)
            });
        } else {
            this.noteService.createNote(this.formNote, this.selectedFile).subscribe({
                next: () => {
                    this.clearForm();
                    this.loadNotes();
                },
                error: (err) => console.error('Erro ao criar nota:', err)
            });
        }
    }

    prepareEdit(note: Note): void {
        this.isEditing = true;
        this.formNote = { ...note };
        this.selectedFile = null;
        this.removeCurrentAttachment = false;
    }

    deleteNote(id: number | undefined, event: Event): void {
        event.stopPropagation();
        if (!id) return;

        if (confirm('Deseja excluir permanentemente esta anotação?')) {
            this.noteService.deleteNote(id).subscribe({
                next: () => {
                    if (this.formNote.id === id) {
                        this.clearForm();
                    }
                    this.loadNotes();
                },
                error: (err) => console.error('Erro ao deletar nota:', err)
            });
        }
    }

    getAttachmentUrl(noteId: number | undefined): string {
        if (!noteId) return '';
        return this.noteService.getAttachmentUrl(noteId);
    }
    openAttachment(note: Note): void {
        if (!note.id) return;

        this.noteService.downloadAttachment(note.id).subscribe({
            next: (blob: Blob) => {
                const url = window.URL.createObjectURL(blob);
                window.open(url, '_blank');
                // Libera a memória depois de um tempo, já que a aba já abriu com o conteúdo
                setTimeout(() => window.URL.revokeObjectURL(url), 10000);
            },
            error: (err) => {
                console.error('Erro ao abrir anexo:', err);
                alert('Não foi possível abrir o anexo.');
            }
        });
    }

    clearForm(): void {
        this.isEditing = false;
        this.formNote = {
            title: '',
            content: '',
            referenceDate: new Date().toISOString().split('T')[0]
        };
        this.selectedFile = null;
        this.removeCurrentAttachment = false;
    }
}