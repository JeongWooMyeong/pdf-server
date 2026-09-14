package com.example.pdf_server.pdf.service.impl;


import com.example.pdf_server.pdf.dto.PdfJobDto;
import com.example.pdf_server.pdf.enums.PdfJobStatus;
import com.example.pdf_server.pdf.service.PdfJobService;
import com.example.pdf_server.pdf.service.dao.mssql.PdfJobMapper;
import com.example.pdf_server.pdf.storage.MinioStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfJobServiceImpl implements PdfJobService {

    private final PdfJobMapper pdfJobMapper;

    private final MinioStorageService minioStorageService;

    @Override
    public void insertJob(
            PdfJobDto dto
    ) {

        pdfJobMapper.insertJob(dto);

    }


    @Override
    public void saveResult(
            String jobid,
            String ordno,
            byte[] result,
            String extension
    ) {

        try {

            String filename;

            /*
             * 단건 PDF
             *
             * 예:
             * TP051997496079.pdf
             */
            if (".pdf".equalsIgnoreCase(extension)) {

                filename =
                        ordno + ".pdf";

            }
            /*
             * 다건 ZIP
             *
             * 예:
             * fdca5902-fe73-40d7-ac03-2aaf946fe668.zip
             */
            else if (".zip".equalsIgnoreCase(extension)) {

                filename =
                        jobid + ".zip";

            }
            else {

                throw new IllegalArgumentException(
                        "지원하지 않는 확장자: " + extension
                );
            }


            /*
             * MinIO Object 경로
             */
            String objectName =
                    "pdf/" + filename;


            /*
             * MinIO 저장
             */
            minioStorageService.upload(
                    objectName,
                    result,
                    getContentType(extension)
            );


            /*
             * DB 저장
             */
            pdfJobMapper.updateResult(
                    jobid,
                    objectName,
                    filename
            );


        } catch (Exception e) {

            throw new RuntimeException(
                    "PDF 파일 MinIO 저장 실패",
                    e
            );
        }
    }


    /**
     * 확장자에 따른 Content-Type
     */
    private String getContentType(
            String extension
    ) {

        if (".pdf".equalsIgnoreCase(extension)) {

            return "application/pdf";

        }

        if (".zip".equalsIgnoreCase(extension)) {

            return "application/zip";

        }

        return "application/octet-stream";

    }


    @Override
    public void start(
            String jobid
    ) {

        pdfJobMapper.updateStart(
                jobid,
                PdfJobStatus.PROCESSING.name()
        );

    }


    @Override
    public void complete(
            String jobid
    ) {

        pdfJobMapper.updateComplete(
                jobid,
                PdfJobStatus.COMPLETE.name()
        );

    }


    @Override
    public void fail(
            String jobid,
            String errorMessage
    ) {

        pdfJobMapper.updateFail(
                jobid,
                PdfJobStatus.FAIL.name(),
                errorMessage
        );

    }


    @Override
    public PdfJobDto findByJobid(
            String jobid
    ) {

        return pdfJobMapper.selectJob(
                jobid
        );

    }


    @Override
    public List<PdfJobDto> findTimeoutJobs() {

        return pdfJobMapper.findTimeoutJobs();

    }


    @Override
    public void updateHeartbeat(
            String jobid
    ) {

        pdfJobMapper.updateHeartbeat(
                jobid
        );

    }

}