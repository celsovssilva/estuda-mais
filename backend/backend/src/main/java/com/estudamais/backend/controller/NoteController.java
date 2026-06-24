package com.estudamais.backend.controller;

import com.estudamais.backend.entity.Note;
import com.estudamais.backend.entity.User;
import com.estudamais.backend.request.NoteRequest;
import com.estudamais.backend.response.NoteResponse;
import com.estudamais.backend.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/note")
public class NoteController {
    @Autowired
    private NoteService noteService;

    @PostMapping("/create")
    public ResponseEntity<NoteResponse> create(@RequestBody @Valid NoteRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        NoteResponse response = noteService.createNote(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/getByDateNote")
    public ResponseEntity<List<NoteResponse>> byUser(Authentication authentication, LocalDate date){
        User user = (User) authentication.getPrincipal();
        List<NoteResponse> response = noteService.getNotesByDate(user.getId(),date);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/getByUserNote")
    public ResponseEntity<List<NoteResponse>> byUser(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        List<NoteResponse> response = noteService.getNotesByUser(user.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{noteId}")
    public ResponseEntity<NoteResponse> updateNotes(@PathVariable Long noteId,@RequestBody @Valid NoteRequest request, Authentication authentication){
        User user = (User) authentication.getPrincipal();
        NoteResponse response = noteService.updateNote(user.getId(),noteId ,request);
        return  ResponseEntity.ok(response);
    }
    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> delete(@PathVariable Long noteId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        noteService.deleteNote(user.getId(), noteId);
        return ResponseEntity.noContent().build();
    }

}
