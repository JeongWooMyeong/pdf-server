package com.example.pdf_server;

import com.example.pdf_server.util.NetWorkUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.pdf_server.pdf.service.dao.mssql")
public class PdfServerApplication{

    public static void main(String[] args) throws Exception{

        String ip = NetWorkUtil.getWifiIp();

        System.out.println("현재 서버 IP : " + ip);

        System.setProperty("server-host", ip);

        SpringApplication.run(PdfServerApplication.class, args);
    }

}
