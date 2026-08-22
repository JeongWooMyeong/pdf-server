package com.example.pdf_server.pdf.service.impl;

import com.example.pdf_server.pdf.dto.FontDto;
import com.example.pdf_server.pdf.service.FontService;
import com.example.pdf_server.pdf.service.dao.mssql.FontMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FontServiceImpl implements FontService {

    private final FontMapper fontMapper;

    @Override
    public FontDto findFontByFamily(String fontFamily) {

        return fontMapper.selectFontByFamily(fontFamily);

    }

}