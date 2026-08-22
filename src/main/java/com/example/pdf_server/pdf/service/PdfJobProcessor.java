package com.example.pdf_server.pdf.service;


import com.example.pdf_server.pdf.kafka.event.PdfGenerateJob;

public interface PdfJobProcessor {

    void process(PdfGenerateJob job);

}