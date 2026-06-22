package com.estudamais.backend.service;

import com.estudamais.backend.entity.User;
import com.estudamais.backend.request.NoteRequest;
import com.estudamais.backend.response.NoteResponse;

import java.time.LocalDate;
import java.util.List;

public interface NoteService {
    NoteResponse createNote(User userId, NoteRequest request);
    List<NoteResponse> getNotesByUser(Long userId);
    List<NoteResponse> getNotesByDate(Long userId, LocalDate date);
    NoteResponse updateNote(Long userId, Long noteId, NoteRequest request);
    void deleteNote(Long userId, Long noteId);
}
