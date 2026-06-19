package com.estudamais.backend.request;

import java.time.LocalDate;

public record NoteRequest(String title, String content, LocalDate referenceDate) {}
