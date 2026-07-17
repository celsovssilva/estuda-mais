package com.estudamais.backend.response;

import com.estudamais.backend.entity.Note;
import java.time.LocalDate;

public record NoteResponse(Long id, String title, String content, LocalDate referenceDate, boolean hasAttachment, String attachmentFileName,
                           String attachmentContentType, Long attachmentSize) {
    public NoteResponse(Note note) {
        this(note.getId(), note.getTitle(), note.getContent(), note.getReferenceDate(),
                note.getAttachmentData() != null,
                note.getAttachmentFileName(),
                note.getAttachmentContentType(),
                note.getAttachmentSize()
        );
    }
}
