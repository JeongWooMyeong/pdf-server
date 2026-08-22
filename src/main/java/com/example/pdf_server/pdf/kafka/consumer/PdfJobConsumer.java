package com.example.pdf_server.pdf.kafka.consumer;

import com.example.pdf_server.pdf.kafka.event.PdfGenerateJob;
import com.example.pdf_server.pdf.service.PdfJobProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PdfJobConsumer {


    private final PdfJobProcessor pdfJobProcessor;


    @KafkaListener(
            topics = "pdf-generate-request",
            groupId = "pdf-worker"
    )
    public void consume(PdfGenerateJob job) {

        pdfJobProcessor.process(job);
    }
}