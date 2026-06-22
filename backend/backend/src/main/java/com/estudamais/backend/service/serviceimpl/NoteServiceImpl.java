package com.estudamais.backend.service.serviceimpl;

import com.estudamais.backend.entity.Note;
import com.estudamais.backend.entity.User;
import com.estudamais.backend.repository.NoteRepository;
import com.estudamais.backend.request.NoteRequest;
import com.estudamais.backend.response.NoteResponse;
import com.estudamais.backend.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

public class NoteServiceImpl implements NoteService {
    @Autowired
    private NoteRepository noteRepository;

    @Override
    public NoteResponse createNote(User userId, NoteRequest request) {
        Note note = Note.builder()
                .userId(userId.getId())
                .title(request.title())
                .content(request.content())
                .referenceDate(request.referenceDate())
                .build();
        return new NoteResponse(noteRepository.save(note));
    }

    @Override
    public List<NoteResponse> getNotesByUser(Long userId) {
        return noteRepository.findByUserId(userId).stream()
                .map(NoteResponse::new)
                .toList();
    }

    @Override
    public List<NoteResponse> getNotesByDate(Long userId, LocalDate date) {
        return noteRepository.findByUserIdAndReferenceDate(userId, date).stream()
                .map(NoteResponse::new)
                .toList();
    }

    @Override
    public NoteResponse updateNote(Long userId, Long noteId, NoteRequest request) {
        Note note = noteRepository.findById(noteId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Note not found"));

        note.setTitle(request.title());
        note.setContent(request.content());
        note.setReferenceDate(request.referenceDate());

        return new NoteResponse(noteRepository.save(note));
    }

    @Override
    public void deleteNote(Long userId, Long noteId) {
        Note note = noteRepository.findById(noteId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Note not found"));
        noteRepository.delete(note);
    }
}
