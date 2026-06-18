package com.estudamais.backend.repository;

import com.estudamais.backend.entity.ReviewMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewMaterialRepository extends JpaRepository<ReviewMaterial, Long> {
    List<ReviewMaterial> findAllByUserId(Long userId);
}