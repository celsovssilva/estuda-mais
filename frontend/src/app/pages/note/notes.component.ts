import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NoteService } from '../../core/services/note/note.service';
import { Note } from '../../core/models/note.models';
import { NavbarComponent } from "../../app/shared/navbar/navbar.component";

export interface ToastNotification {
    message: string;
    type: 'error' | 'success' | 'warning';
}

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

    toast: ToastNotification | null = null;
    private toastTimeout: any;


    pendingDeleteId: number | null = null;

    ngOnInit(): void {
        this.loadNotes();
    }



    private extractErrorMessage(err: any, fallbackMessage: string): string {
        if (typeof err?.error === 'string') return err.error;
        return err?.error?.message || err?.error?.error || err?.message || fallbackMessage;
    }

    showToast(message: string, type: 'error' | 'success' | 'warning' = 'error'): void {
        this.toast = { message, type };
        if (this.toastTimeout) clearTimeout(this.toastTimeout);
        this.toastTimeout = setTimeout(() => {
            this.toast = null;
        }, 4000);
    }

    closeToast(): void {
        this.toast = null;
    }



    loadNotes(): void {
        this.noteService.getNotesByUser().subscribe({
            next: (res: Note[]) => {
                this.allNotes = res || [];
            },
            error: (err) => {
                const msg = this.extractErrorMessage(err, 'Erro ao carregar notas.');
                this.showToast(msg, 'error');
            }
        });
    }

    onFileSelected(event: Event): void {
        const input = event.target as HTMLInputElement;
        if (input.files && input.files.length > 0) {
            const file = input.files[0];
            if (file.size > 5 * 1024 * 1024) {
                this.showToast('O arquivo deve ter no máximo 5MB.', 'warning');
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
            this.showToast('Por favor, preencha o título e o conteúdo da anotação.', 'warning');
            return;
        }

        if (this.isEditing && this.formNote.id) {
            this.noteService.updateNote(this.formNote.id, this.formNote, this.selectedFile, this.removeCurrentAttachment).subscribe({
                next: () => {
                    this.showToast('Anotação atualizada com sucesso!', 'success');
                    this.clearForm();
                    this.loadNotes();
                },
                error: (err) => {
                    const msg = this.extractErrorMessage(err, 'Erro ao atualizar nota.');
                    this.showToast(msg, 'error');
                }
            });
        } else {
            this.noteService.createNote(this.formNote, this.selectedFile).subscribe({
                next: () => {
                    this.showToast('Anotação criada com sucesso!', 'success');
                    this.clearForm();
                    this.loadNotes();
                },
                error: (err) => {
                    const msg = this.extractErrorMessage(err, 'Erro ao criar nota.');
                    this.showToast(msg, 'error');
                }
            });
        }
    }

    prepareEdit(note: Note): void {
        this.isEditing = true;
        this.formNote = {...note};
        this.selectedFile = null;
        this.removeCurrentAttachment = false;
    }

    deleteNote(id: number | undefined, event: Event): void {
        event.stopPropagation();
        if (!id) return;
        this.pendingDeleteId = id;
    }

    confirmDelete(): void {
        if (!this.pendingDeleteId) return;
        const idToDelete = this.pendingDeleteId;
        this.pendingDeleteId = null;

        this.noteService.deleteNote(idToDelete).subscribe({
            next: () => {
                if (this.formNote.id === idToDelete) {
                    this.clearForm();
                }
                this.showToast('Anotação excluída com sucesso!', 'success');
                this.loadNotes();
            },
            error: (err) => {
                const msg = this.extractErrorMessage(err, 'Erro ao deletar nota.');
                this.showToast(msg, 'error');
            }
        });
    }

    cancelDelete(): void {
        this.pendingDeleteId = null;
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
                setTimeout(() => window.URL.revokeObjectURL(url), 10000);
            },
            error: (err) => {
                const msg = this.extractErrorMessage(err, 'Não foi possível abrir o anexo.');
                this.showToast(msg, 'error');
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