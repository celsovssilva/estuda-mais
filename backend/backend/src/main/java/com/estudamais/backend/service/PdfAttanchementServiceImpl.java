package com.estudamais.backend.service;

import com.estudamais.backend.entity.PdfAttachment;
import com.estudamais.backend.repository.PdfAttachementRepository;
import com.estudamais.backend.response.PdfAttanchementResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public class PdfAttanchementServiceImpl implements PdfAttanchemmentService {
    @Autowired
    private PdfAttachementRepository attachmentRepository;

    @Override
    public PdfAttanchementResponse storeAttachment(Long userId, MultipartFile file) throws IOException {
        if (!"application/pdf".equals(file.getContentType())) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }

        PdfAttachment attachment = PdfAttachment.builder()
                .userId(userId)
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .fileData(file.getBytes())
                .build();

        return new PdfAttanchementResponse(attachmentRepository.save(attachment));
    }

    @Override
    public PdfAttachment getAttachment(Long userId, Long fileId) {
        return attachmentRepository.findById(fileId)
                .filter(file -> file.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("File not found or unauthorized"));
    }

    @Override
    public List<PdfAttanchementResponse> getAttachmentsByUser(Long userId) {
        return attachmentRepository.findByUserId(userId).stream()
                .map(PdfAttanchementResponse::new)
                .toList();
    }

    @Override
    public void deleteAttachment(Long userId, Long fileId) {
        PdfAttachment attachment = attachmentRepository.findById(fileId)
                .filter(file -> file.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("File not found or unauthorized"));
        attachmentRepository.delete(attachment);
    }
}
