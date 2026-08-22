package com.example.pdf_server.pdf;


import com.example.pdf_server.pdf.dto.Pppo2000DetailDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
public class PdfGenerator {

    private final PdfImageRenderer pdfImageRenderer;


    private final PdfTextRenderer pdfTextRenderer;



    public PdfGenerator(
            PdfImageRenderer pdfImageRenderer,
            PdfTextRenderer pdfTextRenderer
    ){

        this.pdfImageRenderer = pdfImageRenderer;
        this.pdfTextRenderer = pdfTextRenderer;

    }





    public byte[] generate(
            List<Pppo2000DetailDto> detailList
    ){

        try(
                ByteArrayOutputStream baos =
                        new ByteArrayOutputStream()
        ){


            ObjectMapper mapper =
                    new ObjectMapper();



            PdfDocument pdfDoc =
                    new PdfDocument(
                            new PdfWriter(
                                    baos
                            )
                    );




            for(Pppo2000DetailDto detail : detailList){



                Map<String,Object> data =
                        mapper.readValue(
                                detail.getData(),
                                Map.class
                        );



                List<Map<String,Object>> fields =
                        mapper.readValue(
                                detail.getCanvasData(),
                                List.class
                        );



                /*
                 *
                 * 렌더 순서
                 *
                 * 1. BACKGROUND
                 * 2. IMAGE
                 * 3. TEXT
                 *
                 */


                List<Map<String,Object>> renderFields =
                        new ArrayList<>();




                // BACKGROUND

                for(Map<String,Object> field : fields){


                    String imageType =
                            String.valueOf(
                                    field.get("imageType")
                            );


                    if("BACKGROUND".equals(imageType)){


                        renderFields.add(field);


                    }


                }





                // IMAGE

                for(Map<String,Object> field : fields){


                    String type =
                            String.valueOf(
                                    field.get("type")
                            );


                    String imageType =
                            String.valueOf(
                                    field.get("imageType")
                            );



                    if(
                            !"BACKGROUND".equals(imageType)
                                    &&
                                    "image".equals(type)
                    ){


                        renderFields.add(field);


                    }


                }





                // TEXT

                for(Map<String,Object> field : fields){


                    String type =
                            String.valueOf(
                                    field.get("type")
                            );


                    if("text".equals(type)){


                        renderFields.add(field);


                    }


                }






                /*
                 *
                 * mm -> pt
                 *
                 */


                float mmToPt =
                        72f / 25.4f;



                float pdfWidth =
                        detail.getPaperwidth()
                                *
                                mmToPt;



                float pdfHeight =
                        detail.getPaperheight()
                                *
                                mmToPt;




                PageSize pageSize =
                        new PageSize(
                                pdfWidth,
                                pdfHeight
                        );




                PdfPage page =
                        pdfDoc.addNewPage(
                                pageSize
                        );





                float canvasWidth = 800f;

                float canvasHeight = 600f;




                float scaleX =
                        pdfWidth / canvasWidth;



                float scaleY =
                        pdfHeight / canvasHeight;






                for(Map<String,Object> field :
                        renderFields){



                    String type =
                            String.valueOf(
                                    field.get("type")
                            );




                    if("image".equals(type)){



                        pdfImageRenderer.render(
                                page,
                                field,
                                data,
                                pdfWidth,
                                pdfHeight,
                                scaleX,
                                scaleY
                        );



                    }
                    else if("text".equals(type)){



                        pdfTextRenderer.render(
                                page,
                                field,
                                data,
                                pdfWidth,
                                pdfHeight,
                                scaleX,
                                scaleY
                        );



                    }


                }




            }





            pdfDoc.close();



            return baos.toByteArray();



        }
        catch(Exception e){


            throw new RuntimeException(
                    "PDF 생성 실패",
                    e
            );


        }


    }


}