package com.estudamais.backend.repository;


import com.estudamais.backend.entity.VideoConsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoConsumoRepository extends JpaRepository<VideoConsumo, Long> {
    Optional<VideoConsumo> findByVideoId(String videoId);
}
