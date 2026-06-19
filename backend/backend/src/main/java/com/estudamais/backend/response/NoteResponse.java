package com.estudamais.backend.response;

import com.estudamais.backend.entity.Note;
import java.time.LocalDate;

public record NoteResponse(Long id, String title, String content, LocalDate referenceDate) {
    public NoteResponse(Note note) {
        this(note.getId(), note.getTitle(), note.getContent(), note.getReferenceDate());
    }
}
