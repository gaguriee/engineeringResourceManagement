package com.example.smstest.domain.support.controller;

import com.example.smstest.domain.support.dto.SupportResponse;
import com.example.smstest.domain.support.repository.SupportRepository;
import com.example.smstest.domain.support.service.PdfService;
import com.google.api.client.util.IOUtils;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pdf")
public class PdfController {

    private final PdfService pdfService;
    private final SupportRepository supportRepository;

    @GetMapping("/generate")
    public void generatePdf(@RequestParam(required = false) Long supportId, HttpServletResponse response) throws IOException, DocumentException {

        SupportResponse supportResponse = SupportResponse.entityToResponse(supportRepository.findById(supportId).get());

        // PDF 생성 및 다운로드
        String pdfFilePath = "support_history.pdf";
        pdfService.generatePdf(supportResponse, pdfFilePath);

        // HTTP 응답 설정

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" + pdfFilePath);
        response.setHeader("Content-Length", String.valueOf(new File(pdfFilePath).length()));

        // PDF 파일 스트림 전송
        try (InputStream is = new FileInputStream(pdfFilePath);
             OutputStream os = response.getOutputStream()) {
            IOUtils.copy(is, os);
        }

        // 생성된 PDF 파일 삭제
        Files.deleteIfExists(Paths.get(pdfFilePath));
    }
}

