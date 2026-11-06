package com.estudamais.backend.entity;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "video_resumos")
@Getter
@Setter
@NoArgsConstructor
public class VideoConsumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String videoId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String conteudoResumo;
}
