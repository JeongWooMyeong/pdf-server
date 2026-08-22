package com.example.pdf_server.pdf.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FontDto {
    private Integer fontid;


    private String fontfamily;


    private String fontfilename;

    private String fontfullname;


    private String fontfilepath;


    private Integer fontweight;


    private String fontstyle;


    private String useyn;


    private String regid;


    private LocalDateTime regdt;

}
