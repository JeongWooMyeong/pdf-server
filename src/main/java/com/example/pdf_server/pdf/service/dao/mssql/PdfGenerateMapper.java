package com.example.pdf_server.pdf.service.dao.mssql;

import com.example.pdf_server.pdf.dto.Pppo2000DetailDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PdfGenerateMapper {

    List<Pppo2000DetailDto> selectRequestDetailList(String ordno);

}