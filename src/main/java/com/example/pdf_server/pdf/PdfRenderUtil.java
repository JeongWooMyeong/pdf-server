package com.example.pdf_server.pdf;

import com.itextpdf.kernel.font.PdfFont;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PdfRenderUtil {


    public static float getX(
            Map<String,Object> field,
            float scale
    ){

        return ((Number)field.get("x"))
                .floatValue()
                * scale;

    }



    public static float getWidth(
            Map<String,Object> field,
            float scale
    ){

        return ((Number)field.get("width"))
                .floatValue()
                * scale;

    }



    public static List<Map<String,Object>> sortFields(
            List<Map<String,Object>> fields
    ){


        List<Map<String,Object>> result =
                new ArrayList<>();


        for(Map<String,Object> field : fields){

            if(
                    "BACKGROUND".equals(
                            String.valueOf(field.get("imageType")))
            ){

                result.add(field);

            }

        }



        for(Map<String,Object> field : fields){

            if(
                    !"BACKGROUND".equals(
                            String.valueOf(field.get("imageType")))
            ){

                result.add(field);

            }

        }


        return result;

    }

    /*
     * ====================================
     * 텍스트 영역 맞춤 폰트 계산
     * Preview와 동일
     * ====================================
     */
    /*
     * ====================================
     * PDF 텍스트 영역 맞춤 폰트 계산
     * iText 실제 폭 기준
     * ====================================
     */
    public static float getFitFontSize(
            String text,
            PdfFont font,
            float width,
            float height,
            float fontSize
    ){

        float size = fontSize;


        while(size > 5){


            float totalHeight = 0;


            String[] lines =
                    text.split("\n");


            for(String line : lines){

                totalHeight += size * 1.2f;

            }



            if(totalHeight <= height){

                break;

            }


            size -= 1;

        }


        return size;
    }

    public static String wrapText(
            String text,
            PdfFont font,
            float fontSize,
            float width
    ){

        StringBuilder result =
                new StringBuilder();

        float currentWidth = 0;


        // Konva 기준 한글 폭 보정
        float charWidth = fontSize;



        for(char ch : text.toCharArray()){


            if(currentWidth + charWidth > width){

                result.append("\n");

                currentWidth = 0;
            }


            result.append(ch);

            currentWidth += charWidth;

        }


        return result.toString();
    }

}