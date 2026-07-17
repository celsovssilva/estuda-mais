import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Note } from '../../models/note.models';
import { environment } from "../../../../environments/environment.prod";

@Injectable({
    providedIn: 'root'
})
export class NoteService {
    private http = inject(HttpClient);
    private apiUrl = `${environment.apiUrl}/api/note`;

    createNote(note: Note, file?: File | null): Observable<Note> {
        const formData = new FormData();
        formData.append('note', new Blob([JSON.stringify(note)], { type: 'application/json' }));
        if (file) {
            formData.append('file', file);
        }
        return this.http.post<Note>(`${this.apiUrl}/create`, formData);
    }

    getNotesByUser(): Observable<Note[]> {
        return this.http.get<Note[]>(`${this.apiUrl}/getByUserNote`);
    }

    updateNote(id: number, note: Note, file?: File | null, removeAttachment: boolean = false): Observable<Note> {
        const formData = new FormData();
        formData.append('note', new Blob([JSON.stringify(note)], { type: 'application/json' }));
        if (file) {
            formData.append('file', file);
        }
        return this.http.put<Note>(`${this.apiUrl}/update/${id}?removeAttachment=${removeAttachment}`, formData);
    }

    deleteNote(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/delete/${id}`);
    }

    getAttachmentUrl(noteId: number): string {
        return `${this.apiUrl}/${noteId}/attachment`;
    }
}