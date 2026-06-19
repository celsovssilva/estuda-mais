package com.estudamais.backend.service;

import com.estudamais.backend.entity.PdfAttachment;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import com.estudamais.backend.response.PdfAttanchementResponse;

public interface PdfAttanchemmentService {
    PdfAttanchementResponse storeAttachment(Long userId, MultipartFile file) throws IOException;
    PdfAttachment getAttachment(Long userId, Long fileId);
    List<PdfAttanchementResponse> getAttachmentsByUser(Long userId);
    void deleteAttachment(Long userId, Long fileId);
}
