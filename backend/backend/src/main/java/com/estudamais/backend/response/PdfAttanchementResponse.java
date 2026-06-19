package com.estudamais.backend.response;

import com.estudamais.backend.entity.PdfAttachment;

public record PdfAttanchementResponse(Long id, String fileName, String fileType) {
    public PdfAttanchementResponse(PdfAttachment attachment) {
        this(attachment.getId(), attachment.getFileName(), attachment.getFileType());
    }
}
