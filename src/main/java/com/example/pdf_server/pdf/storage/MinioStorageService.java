package com.example.pdf_server.pdf.storage;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MinioStorageService {

    private final MinioClient minioClient;

    @Value("${storage.bucket}")
    private String bucketName;


    /**
     * 파일 업로드
     */
    public void upload(
            String objectName,
            byte[] data,
            String contentType
    ) throws Exception {

        try (
                ByteArrayInputStream inputStream =
                        new ByteArrayInputStream(data)
        ) {

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(
                                    inputStream,
                                    (long)data.length,
                                    10 * 1024 * 1024L
                            )
                            .contentType(contentType)
                            .build()
            );
        }
    }


    /**
     * 파일 다운로드
     */
    public byte[] download(
            String objectName
    ) throws Exception {

        try (
                InputStream inputStream =
                        minioClient.getObject(
                                GetObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(objectName)
                                        .build()
                        )
        ) {

            return inputStream.readAllBytes();
        }
    }


    /**
     * InputStream 반환
     *
     * 큰 파일을 byte[]로 전부 메모리에 올리지 않고
     * 직접 처리할 때 사용
     */
    public InputStream getInputStream(
            String objectName
    ) throws Exception {

        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }


    /**
     * 파일 존재 여부
     */
    public boolean exists(
            String objectName
    ) {

        try {

            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );

            return true;

        } catch (Exception e) {

            return false;
        }
    }


    /**
     * 파일 삭제
     */
    public void delete(
            String objectName
    ) throws Exception {

        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }
}
