package com.example.pdf_server.pdf.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdfGenerateResultData {

    /*
     * 단건  : PDF byte[]
     * 다건  : ZIP byte[]
     */
    private byte[] result;


    /*
     * Preview 파일 목록
     */
    private List<PdfPreviewFile> files;


    /*
     * 다건 전체 인쇄용 PDF
     *
     * 단건 : null
     * 다건 : 여러 PDF를 merge한 all.pdf byte[]
     */
    private byte[] allPdf;
}