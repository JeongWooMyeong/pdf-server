package com.example.pdf_server.pdf.kafka.producer;

import com.example.pdf_server.pdf.kafka.event.PdfGenerateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfResultProducer {

    private static final String TOPIC =
            "pdf-generate-result";

    private final KafkaTemplate<String, PdfGenerateResult> kafkaTemplate;


    public void sendComplete(
            String jobId,
            String downloadUrl
    ) {

        PdfGenerateResult result =
                new PdfGenerateResult(
                        jobId,
                        "COMPLETE",
                        downloadUrl,
                        null
                );


        kafkaTemplate.send(
                TOPIC,
                jobId,
                result
        );


        log.info(
                "PDF 생성 완료 이벤트 발행. jobId={}",
                jobId
        );
    }


    public void sendFail(
            String jobId,
            String message
    ) {

        PdfGenerateResult result =
                new PdfGenerateResult(
                        jobId,
                        "FAIL",
                        null,
                        message
                );


        kafkaTemplate.send(
                TOPIC,
                jobId,
                result
        );


        log.info(
                "PDF 생성 실패 이벤트 발행. jobId={}",
                jobId
        );
    }

}