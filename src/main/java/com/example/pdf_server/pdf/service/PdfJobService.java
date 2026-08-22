package com.example.pdf_server.pdf.service;


import com.example.pdf_server.pdf.dto.PdfJobDto;

import java.util.List;

public interface PdfJobService {

    void insertJob(
            PdfJobDto dto
    );


    void saveResult(
            String jobid,
            byte[] result,
            String extension
    );


    void start(
            String jobid
    );


    void complete(
            String jobid
    );


    void fail(
            String jobid,
            String errorMessage
    );


    PdfJobDto findByJobid(
            String jobid
    );

    List<PdfJobDto> findTimeoutJobs();

    void updateHeartbeat(
            String jobid
    );

}