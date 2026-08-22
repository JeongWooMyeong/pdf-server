package com.example.pdf_server.pdf.kafka.consumer;


import com.example.pdf_server.pdf.kafka.event.PdfGenerateJob;
import com.example.pdf_server.pdf.kafka.producer.PdfResultProducer;
import com.example.pdf_server.pdf.service.PdfJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/*
궁금증
.DLT 토픽은 자동으로 만들어지는건지? 아니면 내가 토픽을 수동으로 만들어야하는지?
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfJobDlqConsumer {

    private final PdfJobService pdfJobService;

    private final PdfResultProducer pdfResultProducer;


    @KafkaListener(
            topics = "pdf-generate-request.DLT",
            groupId = "pdf-worker-dlt"
    )
    public void consume(PdfGenerateJob job) {

        String jobId = job.getJobid();

        log.error(
                "PDF 작업 최종 실패. jobId={}",
                jobId
        );

        try {

            // 최종 실패 상태 저장
            pdfJobService.fail(
                    jobId,
                    "PDF 생성 Retry 초과"
            );


            // POP 서버로 실패 결과 전달
            pdfResultProducer.sendFail(
                    jobId,
                    "PDF 생성에 실패했습니다."
            );


        } catch (Exception e) {

            log.error(
                    "DLQ 처리 실패 jobId={}",
                    jobId,
                    e
            );

            throw e;
        }
    }
}