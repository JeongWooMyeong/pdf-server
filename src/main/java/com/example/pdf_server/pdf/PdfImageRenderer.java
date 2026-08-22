package com.example.pdf_server.pdf;


import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Map;



@Component
public class PdfImageRenderer {



    public void render(
            PdfPage page,
            Map<String,Object> field,
            Map<String,Object> data,
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

        if(
                "BACKGROUND".equals(imageType)
                        ||
                        "FIXED".equals(imageType)

        ){


            imageUrl =
                    String.valueOf(
                            field.get("imageUrl")
                    );


        }


        /*
         * FIELD IMAGE
         */

        else if(
                "FIELD".equals(imageType)

        ){


            Object img =
                    data.get(fieldName);



            if(img != null){

                imageUrl =
                        img.toString();

            }

        }




        if(
                imageUrl == null
                        ||
                        "null".equals(imageUrl)

        ){

            return;

        }




        /*
         * 내부 경로 처리
         */

        if(imageUrl.startsWith("/")){


            imageUrl =
                    "http://localhost:8081"
                            +
                            imageUrl;


        }





        ImageData imageData =

                ImageDataFactory.create(
                        new URL(imageUrl)
                );





        float x =

                ((Number)field.get("x"))
                        .floatValue()
                        *
                        scaleX;



        float y =

                ((Number)field.get("y"))
                        .floatValue()
                        *
                        scaleY;




        float width =

                ((Number)field.get("width"))
                        .floatValue()
                        *
                        scaleX;




        float height =

                ((Number)field.get("height"))
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





        PdfCanvas pdfCanvas =

                new PdfCanvas(
                        page
                );





        /*
         * 기존 코드 그대로
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