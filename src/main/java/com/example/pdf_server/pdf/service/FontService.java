package com.example.pdf_server.pdf.service;

import com.example.pdf_server.pdf.dto.FontDto;

public interface FontService {

    FontDto findFontByFamily(String fontFamily);

}