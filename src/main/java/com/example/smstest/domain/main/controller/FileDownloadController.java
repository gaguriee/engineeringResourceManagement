package com.example.smstest.domain.main.controller;

import com.example.smstest.domain.main.service.FileDownloadService;
import com.example.smstest.domain.support.dto.SupportResponse;
import com.example.smstest.domain.support.repository.SupportRepository;
import com.example.smstest.domain.task.entity.Task;
import com.example.smstest.domain.task.repository.TaskRepository;
import com.google.api.client.util.IOUtils;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * 파일 Download 관련 PDF
 */
@Controller
@RequiredArgsConstructor
public class FileDownloadController {

    private final FileDownloadService fileDownloadService;
    private final SupportRepository supportRepository;
    private final TaskRepository taskRepository;

    /**
     * 디테일 페이지 지원 내역 PDF 변환
     * @param supportId
     * @param response
     * @throws IOException
     * @throws DocumentException
     */
    @GetMapping("/generatePdf")
    public void generateSupportPdf(@RequestParam(required = false) Long supportId, HttpServletResponse response) throws IOException, DocumentException {

        SupportResponse supportResponse = SupportResponse.entityToResponse(supportRepository.findById(supportId).get());

        // PDF 생성 및 다운로드
        String pdfFilePath = "support_history.pdf";
        fileDownloadService.generateSupportPdf(supportResponse, pdfFilePath);

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

    /**
     * WBS 작업 히스토리 다운로드
     * @param projectId
     * @param response
     * @throws IOException
     * @throws DocumentException
     */
    @GetMapping("/generateExcel")
    public void generateProjectExcel(@RequestParam(required = false) Long projectId, HttpServletResponse response) throws IOException, DocumentException {

        List<Task> tasks = taskRepository.findAllByProjectIdOrderByEstimatedStartDateAsc(projectId);

        // excel sheet 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1"); // 엑셀 sheet 이름
        sheet.setDefaultColumnWidth(28); // 디폴트 너비 설정

        // header font style
        XSSFFont headerXSSFFont = (XSSFFont) workbook.createFont();
        headerXSSFFont.setColor(new XSSFColor(new byte[]{(byte) 255, (byte) 255, (byte) 255}));

        // header cell style
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

        // header data
        int rowCount = 0; // 데이터가 저장될 행
        String headerNames[] = new String[]{"업무명", "예상시작일", "예상마감일", "실제시작일", "실제마감일", "진척율", "산출물"};

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

            String fileName = null ;

            if (!task.getFiles().isEmpty()){
                fileName = task.getFiles().stream().findFirst().get().getOrigFilename();
            }

            String[] data = new String[]{
                    task.getTaskName(),
                    String.valueOf(task.getEstimatedStartDate()),
                    String.valueOf(task.getEstimatedEndDate()),
                    String.valueOf(task.getActualStartDate()),
                    String.valueOf(task.getActualEndDate()),
                    String.valueOf(task.getProgress()),
                    fileName
            };
            bodyDatass.add(data);
        }

        Row bodyRow = null;
        Cell bodyCell = null;

        int previousCategoryId = 0;

        for( int i = 0; i< bodyDatass.size(); i++) {

            int currentCategoryId = tasks.get(i).getCategory().getId();
            if (previousCategoryId != currentCategoryId){
                Row categoryRow = null;
                Cell categoryCell = null;

                sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0,6));

                XSSFCellStyle categoryXssfCellStyle = (XSSFCellStyle) workbook.createCellStyle();

                XSSFFont categoryXSSFFont = (XSSFFont) workbook.createFont();
                categoryXSSFFont.setColor(new XSSFColor(new byte[]{(byte) 0, (byte) 0, (byte) 0}));
                categoryXSSFFont.setBold(true);

                // 테두리 설정
                categoryXssfCellStyle.setBorderLeft(BorderStyle.THIN);
                categoryXssfCellStyle.setBorderRight(BorderStyle.THIN);
                categoryXssfCellStyle.setBorderTop(BorderStyle.THIN);
                categoryXssfCellStyle.setBorderBottom(BorderStyle.THIN);

                // 배경 설정
                categoryXssfCellStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 255, (byte) 255, (byte) 0}));
                categoryXssfCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                categoryXssfCellStyle.setFont(categoryXSSFFont);

                categoryRow = sheet.createRow(rowCount++);
                categoryCell = categoryRow.createCell(0);
                categoryCell.setCellValue(tasks.get(i).getCategory().getName()); // 데이터 추가
                categoryCell.setCellStyle(categoryXssfCellStyle); // 스타일 추가

            }

            previousCategoryId = currentCategoryId;

            String[] bodyDatas = bodyDatass.get(i);
            bodyRow = sheet.createRow(rowCount++);

            for(int j=0; j<bodyDatas.length; j++) {

                bodyCell = bodyRow.createCell(j);
                bodyCell.setCellValue(bodyDatas[j]); // 데이터 추가
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

