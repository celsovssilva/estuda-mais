package com.estudamais.backend.response;

public record PdfAttanchementResponse(Long id, String fileName, String fileType) {
    public PdfAttanchementResponse(PdfAttachment attachment) {
        this(attachment.getId(), attachment.getFileName(), attachment.getFileType());
    }
}
