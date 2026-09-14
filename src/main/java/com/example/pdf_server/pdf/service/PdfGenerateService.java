package com.example.pdf_server.pdf.service;


import com.example.pdf_server.pdf.kafka.event.PdfGenerateJob;
import com.example.pdf_server.pdf.kafka.event.PdfGenerateResultData;

import java.util.List;

public interface PdfGenerateService {

    byte[] generatePdf(String ordno);
    byte[] generateZip(List<String> ordnos);
    PdfGenerateResultData generate(
            PdfGenerateJob.GenerateType type,
            List<String> ordnos,
            String jobId
    );

}
