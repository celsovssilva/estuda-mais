package com.estudamais.backend.controller;

import com.estudamais.backend.entity.Note;
import com.estudamais.backend.entity.User;
import com.estudamais.backend.request.NoteRequest;
import com.estudamais.backend.response.NoteResponse;
import com.estudamais.backend.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/note")
public class NoteController {
    @Autowired
    private NoteService noteService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NoteResponse> create(
            @RequestPart("note") @Valid NoteRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Authentication authentication) throws IOException {
        User user = (User) authentication.getPrincipal();
        NoteResponse response = noteService.createNote(user, request, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/getByDateNote")
    public ResponseEntity<List<NoteResponse>> byUser(Authentication authentication, LocalDate date) {
        User user = (User) authentication.getPrincipal();
        List<NoteResponse> response = noteService.getNotesByDate(user.getId(), date);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getByUserNote")
    public ResponseEntity<List<NoteResponse>> byUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<NoteResponse> response = noteService.getNotesByUser(user.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/update/{noteId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NoteResponse> updateNotes(
            @PathVariable Long noteId,
            @RequestPart("note") @Valid NoteRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "removeAttachment", defaultValue = "false") boolean removeAttachment,
            Authentication authentication) throws IOException {
        User user = (User) authentication.getPrincipal();
        NoteResponse response = noteService.updateNote(user.getId(), noteId, request, file, removeAttachment);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{noteId}")
    public ResponseEntity<Void> delete(@PathVariable Long noteId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        noteService.deleteNote(user.getId(), noteId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{noteId}/attachment")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable Long noteId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Note note = noteService.getNoteEntity(user.getId(), noteId);

        if (note.getAttachmentData() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(note.getAttachmentContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + note.getAttachmentFileName() + "\"")
                .body(note.getAttachmentData());
    }
}