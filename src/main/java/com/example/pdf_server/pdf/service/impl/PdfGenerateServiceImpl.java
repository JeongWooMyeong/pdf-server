package com.example.pdf_server.pdf.service.impl;


import com.example.pdf_server.pdf.PdfGenerator;
import com.example.pdf_server.pdf.dto.Pppo2000DetailDto;
import com.example.pdf_server.pdf.kafka.event.PdfGenerateJob;
import com.example.pdf_server.pdf.kafka.event.PdfGenerateResultData;
import com.example.pdf_server.pdf.kafka.event.PdfPreviewFile;
import com.example.pdf_server.pdf.service.PdfGenerateService;
import com.example.pdf_server.pdf.service.dao.mssql.PdfGenerateMapper;
import com.example.pdf_server.pdf.storage.MinioStorageService;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

@Service
@RequiredArgsConstructor
public class PdfGenerateServiceImpl implements PdfGenerateService {

    private final PdfGenerator PdfGenerators;
    private final PdfGenerateMapper pdfGenerateMapper;
    private final MinioStorageService minioStorageService;

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

    @Override
    public PdfGenerateResultData generate(
            PdfGenerateJob.GenerateType type,
            List<String> ordnos,
            String jobId
    ) {

        try {

            List<PdfPreviewFile> files =
                    new ArrayList<>();


            /*
             * =========================================
             * 모든 PDF를 동일한 경로로 MinIO 저장
             *
             * preview/{jobId}/{ordno}.pdf
             * =========================================
             */

            List<byte[]> pdfList = new ArrayList<>();

            for (String ordno : ordnos) {

                byte[] pdfBytes =
                        generatePdf(ordno);


                String objectName =
                        "preview/"
                                + jobId
                                + "/"
                                + ordno
                                + ".pdf";


                minioStorageService.upload(
                        objectName,
                        pdfBytes,
                        "application/pdf"
                );

                /*
                전체 PDF merge 용
                 */
                pdfList.add(pdfBytes);


                /*
                 * Preview URL
                 */
                String url =
                        "/api/pppo2000/pdf/preview/"
                                + jobId
                                + "/"
                                + ordno;


                files.add(
                        PdfPreviewFile.builder()
                                .ordno(ordno)
                                .url(url)
                                .build()
                );
            }


            /*
             * =========================================
             * 단건
             * =========================================
             */

            if (
                    type ==
                            PdfGenerateJob.GenerateType.PDF
            ) {

                String ordno =
                        ordnos.get(0);


                /*
                 * 단건 PDF도 결과값으로 반환
                 *
                 * 이미 MinIO에 저장했으므로
                 * 다시 생성하지 않음
                 */
                byte[] pdfBytes =
                        minioStorageService.download(
                                "preview/"
                                        + jobId
                                        + "/"
                                        + ordno
                                        + ".pdf"
                        );


                return PdfGenerateResultData.builder()
                        .result(pdfBytes)
                        .files(files)
                        .allPdf(null)
                        .build();
            }


            /*
             * =========================================
             * 다건
             *
             * 개별 PDF는 이미 MinIO에 저장됨.
             * 여기서는 ZIP만 생성
             * 전체 인쇄용 all.pdf 생성
             * =========================================
             */
            byte[] zipBytes;

            try (
                    ByteArrayOutputStream baos =
                            new ByteArrayOutputStream();

                    ZipOutputStream zos =
                            new ZipOutputStream(baos)
            ) {

                for (int i = 0; i < ordnos.size(); i++) {

                    String ordno = ordnos.get(i);

                    byte[] pdfBytes = pdfList.get(i);

                    ZipEntry entry =
                            new ZipEntry(ordno + ".pdf");

                    zos.putNextEntry(entry);

                    zos.write(pdfBytes);

                    zos.closeEntry();
                }

                zos.finish();

                zipBytes = baos.toByteArray();
            }

            /*
             * -----------------------------------------
             * 전체 인쇄용 PDF 생성
             *
             * PDF 순서는 ordnos 순서
             * -----------------------------------------
             */

            byte[] allPdfBytes;

            try (
                    ByteArrayOutputStream baos =
                            new ByteArrayOutputStream()
            ) {

                PDFMergerUtility merger =
                        new PDFMergerUtility();

                merger.setDestinationStream(baos);

                for (byte[] pdfBytes : pdfList) {

                    merger.addSource(
                            new RandomAccessReadBuffer(pdfBytes)
                    );
                }

                merger.mergeDocuments(null);

                allPdfBytes =
                        baos.toByteArray();
            }


            /*
             * =========================================
             * 결과 반환
             * =========================================
             */

            return PdfGenerateResultData.builder()
                    .result(zipBytes)
                    .files(files)
                    .allPdf(allPdfBytes)
                    .build();

        } catch (Exception e) {

            throw new RuntimeException(
                    "PDF 생성 실패",
                    e
            );
        }
    }


}