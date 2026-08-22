package com.example.pdf_server.pdf.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pppo2000DetailDto {
    private String ordno;
    private String templateid;
    private String rgDate;
    //private String data;           // 신청자가 입력한 JSON
    private String data;
    private String title;          // 템플릿 제목
    private String canvasData;     // 템플릿 정의 JSON
    private String thumbnailpath;  // 템플릿 썸네일
    private String createdAt;
    private String updatedAt;
    private float paperwidth;
    private float paperheight;
}