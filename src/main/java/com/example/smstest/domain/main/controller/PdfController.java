package com.example.smstest.domain.main.controller;

import com.example.smstest.domain.project.repository.ProjectRepository;
import com.example.smstest.domain.support.dto.SupportResponse;
import com.example.smstest.domain.support.repository.SupportRepository;
import com.example.smstest.domain.support.service.PdfService;
import com.example.smstest.domain.task.Task;
import com.example.smstest.domain.task.TaskRepository;
import com.google.api.client.util.IOUtils;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pdf")
public class PdfController {

    private final PdfService pdfService;
    private final SupportRepository supportRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    @GetMapping("/generate")
    public void generateSupportPdf(@RequestParam(required = false) Long supportId, HttpServletResponse response) throws IOException, DocumentException {

        SupportResponse supportResponse = SupportResponse.entityToResponse(supportRepository.findById(supportId).get());

        // PDF 생성 및 다운로드
        String pdfFilePath = "support_history.pdf";
        pdfService.generateSupportPdf(supportResponse, pdfFilePath);

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

    @GetMapping("/generateExcel")
    public void generateProjectExcel(@RequestParam(required = false) Long projectId, HttpServletResponse response) throws IOException, DocumentException {

        List<Task> tasks = taskRepository.findAllByProjectIdOrderByEstimatedStartDateAsc(projectId);

        /**
         * excel sheet 생성
         */
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1"); // 엑셀 sheet 이름
        sheet.setDefaultColumnWidth(28); // 디폴트 너비 설정

        /**
         * header font style
         */
        XSSFFont headerXSSFFont = (XSSFFont) workbook.createFont();
        headerXSSFFont.setColor(new XSSFColor(new byte[]{(byte) 255, (byte) 255, (byte) 255}));

        /**
         * header cell style
         */
        XSSFCellStyle headerXssfCellStyle = (XSSFCellStyle) workbook.createCellStyle();

        // 테두리 설정
        headerXssfCellStyle.setBorderLeft(BorderStyle.THIN);
        headerXssfCellStyle.setBorderRight(BorderStyle.THIN);
        headerXssfCellStyle.setBorderTop(BorderStyle.THIN);
        headerXssfCellStyle.setBorderBottom(BorderStyle.THIN);

        // 배경 설정
        headerXssfCellStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 34, (byte) 37, (byte) 41}));
        headerXssfCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerXssfCellStyle.setFont(headerXSSFFont);

        /**
         * body cell style
         */
        XSSFCellStyle bodyXssfCellStyle = (XSSFCellStyle) workbook.createCellStyle();

        // 테두리 설정
        bodyXssfCellStyle.setBorderLeft(BorderStyle.THIN);
        bodyXssfCellStyle.setBorderRight(BorderStyle.THIN);
        bodyXssfCellStyle.setBorderTop(BorderStyle.THIN);
        bodyXssfCellStyle.setBorderBottom(BorderStyle.THIN);

        /**
         * header data
         */
        int rowCount = 0; // 데이터가 저장될 행
        String headerNames[] = new String[]{"진척률", "대분류", "업무명", "예상시작일", "예상마감일", "실제시작일", "실제마감일", "산출물"};

        Row headerRow = null;
        Cell headerCell = null;

        headerRow = sheet.createRow(rowCount++);
        for(int i=0; i<headerNames.length; i++) {
            headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headerNames[i]); // 데이터 추가
            headerCell.setCellStyle(headerXssfCellStyle); // 스타일 추가
        }

        /**
         * body data
         */
        ArrayList<String[]> bodyDatass = new ArrayList<>();

        for (int i = 0; i < tasks.size(); i++){
            Task task = tasks.get(i);
            String[] data = new String[]{
                    String.valueOf(task.getProgress()),
                    task.getCategory().getName(),
                    task.getTaskName(),
                    String.valueOf(task.getEstimatedStartDate()),
                    String.valueOf(task.getEstimatedEndDate()),
                    String.valueOf(task.getActualStartDate()),
                    String.valueOf(task.getActualEndDate()),
                    task.getActualOutput()
            };
            bodyDatass.add(data);
        }

        Row bodyRow = null;
        Cell bodyCell = null;

        for(String[] bodyDatas : bodyDatass) {
            bodyRow = sheet.createRow(rowCount++);

            for(int i=0; i<bodyDatas.length; i++) {
                bodyCell = bodyRow.createCell(i);
                bodyCell.setCellValue(bodyDatas[i]); // 데이터 추가
                bodyCell.setCellStyle(bodyXssfCellStyle); // 스타일 추가
            }
        }

        /**
         * download
         */
        String fileName = "spring_excel_download";

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
        ServletOutputStream servletOutputStream = response.getOutputStream();

        workbook.write(servletOutputStream);
        workbook.close();
        servletOutputStream.flush();
        servletOutputStream.close();
    }
}

