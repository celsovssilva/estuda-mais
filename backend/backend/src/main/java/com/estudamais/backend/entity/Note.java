package com.estudamais.backend.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

@Entity
@Table(name = "tb_notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDate referenceDate;

    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(name = "attachment_data")
    private byte[] attachmentData;
    private String attachmentFileName;

    private String attachmentContentType;

    private Long attachmentSize;
}