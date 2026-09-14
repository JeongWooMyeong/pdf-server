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
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
                        .build();
            }


            /*
             * =========================================
             * 다건
             *
             * 개별 PDF는 이미 MinIO에 저장됨.
             * 여기서는 ZIP만 생성
             * =========================================
             */

            try (
                    ByteArrayOutputStream baos =
                            new ByteArrayOutputStream();

                    ZipOutputStream zos =
                            new ZipOutputStream(baos)
            ) {

                for (String ordno : ordnos) {

                    byte[] pdfBytes =
                            minioStorageService.download(
                                    "preview/"
                                            + jobId
                                            + "/"
                                            + ordno
                                            + ".pdf"
                            );


                    ZipEntry entry =
                            new ZipEntry(
                                    ordno + ".pdf"
                            );

                    zos.putNextEntry(entry);

                    zos.write(pdfBytes);

                    zos.closeEntry();
                }

                zos.finish();


                return PdfGenerateResultData.builder()
                        .result(baos.toByteArray())
                        .files(files)
                        .build();
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "PDF 생성 실패",
                    e
            );
        }
    }


}