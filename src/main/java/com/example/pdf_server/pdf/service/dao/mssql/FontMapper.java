package com.example.pdf_server.pdf.service.dao.mssql;

import com.example.pdf_server.pdf.dto.FontDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FontMapper {

    FontDto selectFontByFamily(String fontFamily);

}