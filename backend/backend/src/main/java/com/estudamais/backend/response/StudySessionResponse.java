package com.estudamais.backend.response;

import com.estudamais.backend.entity.StudySession;
import lombok.*;
import java.time.LocalDate;

public record StudySessionResponse(
        Long id,
        String subject,
        Integer durationMinutes,
        LocalDate studyDate,
        String feedbackMessage
) {



    public StudySessionResponse(StudySession session, String feedbackMessage) {
        this(
                session.getId(),
                session.getSubject(),
                session.getDurationMinutes(),
                session.getStudyDate(),
                feedbackMessage
        );
    }

    public StudySessionResponse(StudySession session) {
        this(session.getId(), session.getSubject(), session.getDurationMinutes(), session.getStudyDate(), null);
    }
}