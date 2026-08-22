package com.example.pdf_server.pdf.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PdfGenerateJob {

    private String jobid;

    /**
     * 생성 대상 주문번호 목록
     */
    private List<String> ordnos;


    /**
     * 단건 PDF인지 ZIP인지
     */
    private GenerateType type;


    public enum GenerateType {
        PDF,
        ZIP
    }
}