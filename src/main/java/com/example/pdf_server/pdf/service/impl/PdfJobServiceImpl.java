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
            byte[] result,
            String extension
    ) {

        try {

            /*
             * 파일명 생성
             */
            String filename =
                    "PDF_"
                            + LocalDateTime.now()
                            .format(
                                    DateTimeFormatter.ofPattern(
                                            "yyyyMMdd_HHmmss"
                                    )
                            )
                            + "_"
                            + UUID.randomUUID()
                            .toString()
                            .substring(0, 6)
                            + extension;


            /*
             * MinIO Object 경로
             *
             * bucket
             *   └── pdf/
             *       └── PDF_xxx.pdf
             */
            String objectName =
                    "pdf/" + filename;


            /*
            minio 공통
             */
            minioStorageService.upload(
                    objectName,
                    result,
                    getContentType(extension)
            );


            /*
             * DB에는 실제 파일 자체가 아니라
             * MinIO Object 정보 저장
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