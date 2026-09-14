package com.example.pdf_server.pdf.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdfGenerateData {

    private byte[] zipBytes;

    private List<PdfPreviewFile> files;
}