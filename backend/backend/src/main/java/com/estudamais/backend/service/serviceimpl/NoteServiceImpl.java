package com.estudamais.backend.service.serviceimpl;

import com.estudamais.backend.entity.Note;
import com.estudamais.backend.entity.User;
import com.estudamais.backend.repository.NoteRepository;
import com.estudamais.backend.request.NoteRequest;
import com.estudamais.backend.response.NoteResponse;
import com.estudamais.backend.service.NoteService;
import io.jsonwebtoken.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
public class NoteServiceImpl implements NoteService {
    @Autowired
    private NoteRepository noteRepository;


    @Override
    public NoteResponse createNote(User user, NoteRequest request, MultipartFile file) throws IOException, java.io.IOException {
        Note.NoteBuilder builder = Note.builder()
                .userId(user.getId())
                .title(request.title())
                .content(request.content())
                .referenceDate(request.referenceDate());

        if (file != null && !file.isEmpty()) {
            builder.attachmentData(file.getBytes())
                    .attachmentFileName(file.getOriginalFilename())
                    .attachmentContentType(file.getContentType())
                    .attachmentSize(file.getSize());
        }

        Note note = builder.build();
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
    public NoteResponse updateNote(Long userId, Long noteId, NoteRequest request,MultipartFile file, boolean removeAttachment) throws java.io.IOException {
        Note note = noteRepository.findById(noteId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Note not found"));

        note.setTitle(request.title());
        note.setContent(request.content());
        note.setReferenceDate(request.referenceDate());
    if(file !=null && !file.isEmpty()){
        note.setAttachmentData(file.getBytes());
        note.setAttachmentFileName(file.getOriginalFilename());
        note.setAttachmentSize(file.getSize());
        note.setAttachmentContentType(file.getContentType());

    } else if (removeAttachment) {
        note.setAttachmentData(null);
        note.setAttachmentFileName(null);
        note.setAttachmentSize(null);
        note.setAttachmentContentType(null);
    }

        return new NoteResponse(noteRepository.save(note));
    }

    @Override
    public void deleteNote(Long userId, Long noteId) {
        Note note = noteRepository.findById(noteId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Note not found"));
        noteRepository.delete(note);
    }

    @Override
    public Note getNoteEntity(Long userId, Long noteId) {
        return noteRepository.findById(noteId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Note not found"));
    }
}
