package com.example.pdf_server.pdf;


import com.example.pdf_server.pdf.dto.FontDto;
import com.example.pdf_server.pdf.service.dao.mssql.FontMapper;
import com.example.pdf_server.pdf.storage.MinioStorageService;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PdfTextRenderer {

    private final FontMapper sytm3000Mapper;

    private final MinioStorageService minioStorageService;


    public PdfTextRenderer(
            FontMapper sytm3000Mapper,
            MinioStorageService minioStorageService
    ) {

        this.sytm3000Mapper =
                sytm3000Mapper;

        this.minioStorageService =
                minioStorageService;
    }


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


        float x =
                ((Number) field.get("x"))
                        .floatValue()
                        * scaleX;


        float y =
                ((Number) field.get("y"))
                        .floatValue()
                        * scaleY;


        float width =
                ((Number) field.get("width"))
                        .floatValue()
                        * scaleX;


        float height =
                ((Number) field.get("height"))
                        .floatValue()
                        * scaleY;


        /*
         * 데이터 값
         */

        Object obj =
                data.get(fieldName);


        String value =
                obj == null
                        ? ""
                        : obj.toString();


        /*
         * 기본 TEXT
         */

        if (value.isEmpty()) {

            Object text =
                    field.get("text");

            if (text != null) {

                value =
                        text.toString();
            }
        }


        /*
         * 폰트 생성
         */

        String fontFamily =
                String.valueOf(
                        field.get("fontFamily")
                );


        PdfFont pdfFont =
                createFont(
                        fontFamily
                );


        /*
         * 폰트 사이즈
         */

        float fontSize = 20;

        Object fs =
                field.get("fontSize");

        if (fs != null) {

            fontSize =
                    ((Number) fs)
                            .floatValue();
        }

        fontSize *= scaleX;


        /*
         * 줄바꿈
         */

        String wrapped =
                PdfRenderUtil.wrapText(
                        value,
                        pdfFont,
                        fontSize,
                        width
                );


        /*
         * 영역 맞춤
         */

        fontSize =
                PdfRenderUtil.getFitFontSize(
                        wrapped,
                        pdfFont,
                        width,
                        height,
                        fontSize
                );


        wrapped =
                PdfRenderUtil.wrapText(
                        value,
                        pdfFont,
                        fontSize,
                        width
                );


        float pdfY =
                pdfHeight
                        - y
                        - height;


        Paragraph paragraph =
                new Paragraph();


        for (String line :
                wrapped.split("\n")) {

            paragraph.add(
                    new Text(line)
                            .setFont(pdfFont)
                            .setFontSize(fontSize)
            );

            paragraph.add("\n");
        }


        paragraph
                .setMargin(0)
                .setPadding(0)
                .setFixedLeading(
                        fontSize * 1.2f
                );


        Canvas canvas =
                new Canvas(
                        new PdfCanvas(page),
                        new Rectangle(
                                x,
                                pdfY,
                                width,
                                height
                        )
                );


        canvas.add(paragraph);

        canvas.close();
    }


    /**
     * MinIO에서 폰트를 가져와
     * iText PdfFont로 생성
     */
    private PdfFont createFont(
            String fontFamily
    ) throws Exception {


        FontDto font =
                sytm3000Mapper
                        .selectFontByFamily(
                                fontFamily
                        );


        String objectName;


        /*
         * DB에 등록된 폰트
         */

        if (
                font != null
                        &&
                        font.getFontfilepath() != null
                        &&
                        !font.getFontfilepath().isBlank()
        ) {

            objectName =
                    font.getFontfilepath();

        }

        /*
         * 폰트가 없으면 기본 폰트
         */

        else {

            objectName =
                    "fonts/NanumGothic.ttf";
        }


        /*
         * DB 경로가 /fonts/xxx.ttf 형태라면
         * MinIO Object 이름으로 사용하기 위해
         * 앞의 / 제거
         */

        if (objectName.startsWith("/upload/")) {

            objectName =
                    objectName.substring(
                            "/upload/".length()
                    );

        }
        else if (objectName.startsWith("/")) {

            objectName =
                    objectName.substring(1);
        }


        /*
         * MinIO 다운로드
         */

        byte[] fontBytes =
                minioStorageService.download(
                        objectName
                );


        /*
         * byte[] → iText PdfFont
         */

        return PdfFontFactory.createFont(
                fontBytes,
                PdfEncodings.IDENTITY_H,
                PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED
        );
    }
}