import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {Note} from "../../models/note.models";

@Injectable({
    providedIn: 'root'
})
export class NoteService {
    private apiUrl = 'http://localhost:8080/api/note';

    constructor(private http: HttpClient) {}

    // Busca as notas do usuário logado
    getNotesByUser(): Observable<Note[]> {
        return this.http.get<Note[]>(`${this.apiUrl}/getByUserNote`);
    }

    // Cria uma nova nota
    createNote(note: Note): Observable<Note> {
        return this.http.post<Note>(`${this.apiUrl}/create`, note);
    }

    // Atualiza uma nota existente
    updateNote(noteId: number, note: Note): Observable<Note> {
        return this.http.put<Note>(`${this.apiUrl}/update/${noteId}`, note);
    }

    // Deleta uma nota
    deleteNote(noteId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/delete/${noteId}`);
    }
}