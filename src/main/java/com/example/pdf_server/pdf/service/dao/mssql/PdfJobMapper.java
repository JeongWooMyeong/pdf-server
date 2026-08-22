package com.example.pdf_server.pdf.service.dao.mssql;


import com.example.pdf_server.pdf.dto.PdfJobDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PdfJobMapper {


    void insertJob(
            PdfJobDto dto
    );


    void updateStart(
            @Param("jobid") String jobid,
            @Param("status") String status
    );


    void updateComplete(
            @Param("jobid") String jobid,
            @Param("status") String status
    );


    void updateFail(
            @Param("jobid") String jobid,
            @Param("status") String status,
            @Param("errorMessage") String errorMessage
    );


    void updateResult(
            @Param("jobid") String jobid,
            @Param("filepath") String filepath,
            @Param("filename") String filename
    );

    PdfJobDto selectJob(
            @Param("jobid")
            String jobid
    );

    List<PdfJobDto> findTimeoutJobs();

    void updateHeartbeat(
            String jobid
    );

}