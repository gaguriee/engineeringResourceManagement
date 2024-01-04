package com.example.smstest.global.filedownload;

import com.example.smstest.domain.support.dto.SupportResponse;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class FileDownloadService {

    public void generateSupportPdf(SupportResponse supportResponse, String filePath) throws IOException, DocumentException {

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        String os = System.getProperty("os.name").toLowerCase();
        String fontPath = null;
        if (os.contains("win")){
            fontPath = "src/main/resources/static/font/NanumGothic.ttf";
        }
        else{
            fontPath = "/home/sysadmin/fonts/NanumGothic.ttf";
        }

        BaseFont baseFont = BaseFont.createFont(
                fontPath,
               BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        Font boldfont = new Font(baseFont,16);
        Font font = new Font(baseFont,12);
        PdfPTable table1 = createTable(4);
        PdfPTable table2 = createTable(3);
        PdfPTable table3 = createTable(1);
        PdfPTable table4 = createTable(1);

        document.add(new Paragraph(new Chunk(supportResponse.getTaskTitle(), boldfont)));

        document.add(Chunk.NEWLINE);

        addCell(table1, "제품명", font);
        addCell(table1, "고객사", font);
        addCell(table1, "업무구분", font);
        addCell(table1, "이슈구분", font);
        addCell(table1, supportResponse.getProductName(), font);
        addCell(table1, supportResponse.getProject().getClient().getName(), font);
        addCell(table1, supportResponse.getState(), font);
        addCell(table1, supportResponse.getIssueType(), font);

        addCell(table2, "지원일자", font);
        addCell(table2, "지원형태", font);
        addCell(table2, "담당엔지니어", font);
        addCell(table2, supportResponse.getSupportDate().toString(), font);
        addCell(table2, supportResponse.getSupportType() + " ("+ supportResponse.getSupportTypeHour()+"h)", font);
        addCell(table2, supportResponse.getEngineerName(), font);

        addCell(table3, "작업요약", font);
        addCell(table3, supportResponse.getTaskSummary(), font);

        addCell(table4, "작업상세", font);
        addCell(table4, supportResponse.getTaskDetails(), font);

        document.add(table1);
        document.add(table2);

        document.add(Chunk.NEWLINE);
        document.add(table3);
        document.add(table4);
        document.add(Chunk.NEWLINE);


        document.close();

    }

    private PdfPTable createTable(int numColumns) {
        PdfPTable table = new PdfPTable(numColumns);
        table.setWidthPercentage(100);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        return table;
    }

    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setPadding(8);
        table.addCell(cell);
    }


}
