package com.example.pdf_server.pdf.service;


import java.util.List;

public interface PdfGenerateService {

    byte[] generatePdf(String ordno);
    byte[] generateZip(List<String> ordnos);

}
