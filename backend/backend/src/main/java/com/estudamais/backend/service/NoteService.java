package com.estudamais.backend.service;

import com.estudamais.backend.entity.Note;
import com.estudamais.backend.entity.User;
import com.estudamais.backend.request.NoteRequest;
import com.estudamais.backend.response.NoteResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface NoteService {
    NoteResponse createNote(User userId, NoteRequest request, MultipartFile file) throws IOException;
    List<NoteResponse> getNotesByUser(Long userId);
    List<NoteResponse> getNotesByDate(Long userId, LocalDate date);
    NoteResponse updateNote(Long userId, Long noteId, NoteRequest request,MultipartFile file, boolean removeAttachment) throws IOException;
    void deleteNote(Long userId, Long noteId);
    Note getNoteEntity(Long userId,Long noteId);
}
