package com.example.pdf_server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.pdf_server.pdf.service.dao.mssql")
public class PdfServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdfServerApplication.class, args);
    }

}
