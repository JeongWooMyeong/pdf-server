package com.example.pdf_server.pdf.service.impl;

import com.example.pdf_server.pdf.service.PdfGenerateService;
import com.example.pdf_server.pdf.dto.PdfJobDto;
import com.example.pdf_server.pdf.kafka.event.PdfGenerateJob;
import com.example.pdf_server.pdf.service.PdfJobProcessor;
import com.example.pdf_server.pdf.service.PdfJobService;
import com.example.pdf_server.pdf.kafka.producer.PdfResultProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfJobProcessorImpl implements PdfJobProcessor {

    private final PdfGenerateService pdfGenerateService;

    private final PdfJobService pdfJobService;

    private final PdfResultProducer pdfResultProducer;


    private final ScheduledExecutorService heartbeatScheduler =
            Executors.newScheduledThreadPool(1);


    @Override
    public void process(PdfGenerateJob job) {

        String jobId = job.getJobid();

        ScheduledFuture<?> heartbeatTask = null;

        log.info(
                "PDF process 시작. jobId={}",
                jobId
        );


        // JOB 조회
        PdfJobDto currentJob =
                pdfJobService.findByJobid(jobId);


        if (currentJob == null) {

            throw new RuntimeException(
                    "PDF JOB 없음"
            );

        }


        // 이미 완료된 JOB이면 중복 처리하지 않음
        if ("COMPLETE".equals(currentJob.getStatus())) {

            log.info(
                    "이미 완료된 작업입니다. jobId={}",
                    jobId
            );

            return;
        }


        try {

            // 작업 시작
            pdfJobService.start(jobId);


            // Heartbeat 시작
            heartbeatTask =
                    heartbeatScheduler.scheduleAtFixedRate(
                            () -> {

                                try {

                                    pdfJobService.updateHeartbeat(
                                            jobId
                                    );

                                    log.info(
                                            "PDF heartbeat update jobId={}",
                                            jobId
                                    );

                                } catch (Exception e) {

                                    log.error(
                                            "heartbeat 실패 jobId={}",
                                            jobId,
                                            e
                                    );

                                }

                            },
                            5,
                            5,
                            TimeUnit.MINUTES
                    );


            // PDF / ZIP 생성
            byte[] result;

            if (
                    job.getType()
                            == PdfGenerateJob.GenerateType.PDF
            ) {

                result =
                        pdfGenerateService.generatePdf(
                                job.getOrdnos().get(0)
                        );

            } else {

                result =
                        pdfGenerateService.generateZip(
                                job.getOrdnos()
                        );

            }


            // 확장자
            String extension =
                    job.getType()
                            == PdfGenerateJob.GenerateType.PDF
                            ? ".pdf"
                            : ".zip";


            // MinIO 저장
            pdfJobService.saveResult(
                    jobId,
                    result,
                    extension
            );


            // 완료
            pdfJobService.complete(jobId);


            // PDF 서버 → Kafka
            pdfResultProducer.sendComplete(
                    jobId,
                    "/api/pppo2000/pdf/preview/" + jobId
            );


        } catch (Exception e) {

            log.error(
                    "PDF 생성 실패 jobId={}",
                    jobId,
                    e
            );


            /*
             * 여기서 FAIL 처리하지 않음.
             *
             * 예외를 Kafka Listener까지 전달해서
             * Kafka Retry가 동작하도록 함.
             */
            throw new RuntimeException(
                    "PDF 생성 실패",
                    e
            );


        } finally {

            if (heartbeatTask != null) {

                heartbeatTask.cancel(true);

            }

        }

    }

}