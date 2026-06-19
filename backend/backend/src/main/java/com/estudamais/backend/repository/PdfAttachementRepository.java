package com.estudamais.backend.repository;

import com.estudamais.backend.entity.PdfAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PdfAttachementRepository extends JpaRepository<PdfAttachment,Long> {
    List<PdfAttachment> findByUserId(Long userId);
}
