package com.example.pdf_server.pdf;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Map;

@Component
public class PdfImageRenderer {

    @Value("${pdf.image-base-url}")
    private String imageBaseUrl;

    public void render(
            PdfPage page,
            Map<String, Object> field,
            Map<String, Object> data,
            float pdfWidth,
            float pdfHeight,
            float scaleX,
            float scaleY
    ) throws Exception {

        String fieldName =
                String.valueOf(
                        field.get("fieldName")
                );

        String imageUrl = null;

        String imageType =
                String.valueOf(
                        field.get("imageType")
                );

        /*
         * BACKGROUND
         * FIXED
         */
        if (
                "BACKGROUND".equals(imageType)
                        ||
                        "FIXED".equals(imageType)
        ) {

            imageUrl =
                    String.valueOf(
                            field.get("imageUrl")
                    );
        }

        /*
         * FIELD IMAGE
         */
        else if (
                "FIELD".equals(imageType)
        ) {

            Object img =
                    data.get(fieldName);

            if (img != null) {

                imageUrl =
                        img.toString();
            }
        }

        /*
         * 이미지 URL이 없는 경우
         */
        if (
                imageUrl == null
                        ||
                        "null".equals(imageUrl)
                        ||
                        imageUrl.isBlank()
        ) {

            return;
        }

        /*
         * 내부 경로 처리
         *
         * 예:
         * /upload/userimage/xxx.png
         *
         * 로컬:
         * http://localhost:8081/upload/userimage/xxx.png
         *
         * Kubernetes:
         * http://myapp-service:8081/upload/userimage/xxx.png
         */
        if (imageUrl.startsWith("/")) {

            imageUrl =
                    imageBaseUrl
                            + imageUrl;
        }

        /*
         * 최종 이미지 URL 로그
         */
        System.out.println(
                "PDF Image URL = " + imageUrl
        );

        /*
         * 이미지 로드
         */
        ImageData imageData =
                ImageDataFactory.create(
                        new URL(imageUrl)
                );

        /*
         * X 좌표
         */
        float x =
                ((Number) field.get("x"))
                        .floatValue()
                        *
                        scaleX;

        /*
         * Y 좌표
         */
        float y =
                ((Number) field.get("y"))
                        .floatValue()
                        *
                        scaleY;

        /*
         * 이미지 Width
         */
        float width =
                ((Number) field.get("width"))
                        .floatValue()
                        *
                        scaleX;

        /*
         * 이미지 Height
         */
        float height =
                ((Number) field.get("height"))
                        .floatValue()
                        *
                        scaleY;

        /*
         * iText 좌표계 변환
         */
        float pdfY =
                pdfHeight
                        -
                        y
                        -
                        height;

        /*
         * PDF Canvas
         */
        PdfCanvas pdfCanvas =
                new PdfCanvas(
                        page
                );

        /*
         * 이미지 삽입
         */
        pdfCanvas.addImageWithTransformationMatrix(
                imageData,
                width,
                0,
                0,
                height,
                x,
                pdfY
        );
    }
}