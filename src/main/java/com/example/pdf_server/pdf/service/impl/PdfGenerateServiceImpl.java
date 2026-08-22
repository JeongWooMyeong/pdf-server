package com.example.pdf_server.pdf.service.impl;


import com.example.pdf_server.pdf.PdfGenerator;
import com.example.pdf_server.pdf.dto.Pppo2000DetailDto;
import com.example.pdf_server.pdf.service.PdfGenerateService;
import com.example.pdf_server.pdf.service.dao.mssql.PdfGenerateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class PdfGenerateServiceImpl implements PdfGenerateService {

    private final PdfGenerator PdfGenerators;
    private final PdfGenerateMapper pdfGenerateMapper;

    @Override
    public byte[] generatePdf(String ordno) {


        List<Pppo2000DetailDto> detailList =
                pdfGenerateMapper.selectRequestDetailList(ordno);



        return PdfGenerators.generate(
                detailList
        );


    }

    @Override
    public byte[] generateZip(List<String> ordnos) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (String ordno : ordnos) {
                byte[] pdfBytes = generatePdf(ordno);
                ZipEntry entry = new ZipEntry(ordno + ".pdf");
                zos.putNextEntry(entry);
                zos.write(pdfBytes);
                zos.closeEntry();
            }

            zos.finish();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("ZIP 생성 실패", e);
        }
    }
}