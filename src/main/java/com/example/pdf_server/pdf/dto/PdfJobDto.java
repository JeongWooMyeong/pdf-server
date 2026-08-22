package com.example.pdf_server.pdf.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PdfJobDto {

    private String jobid;

    private String status;

    private String filepath;

    private String errormessage;

    private Integer retrycount;

    private LocalDateTime createdate;

    private LocalDateTime startdate;

    private LocalDateTime completedate;

    private LocalDateTime updatedate;

}