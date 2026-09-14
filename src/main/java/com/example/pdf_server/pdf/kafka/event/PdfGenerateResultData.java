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
public class PdfGenerateResultData {

    private byte[] result;

    private List<PdfPreviewFile> files;
}