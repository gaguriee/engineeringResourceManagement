package com.example.smstest.domain.task;

import com.example.smstest.domain.file.FileDto;
import com.example.smstest.domain.file.FileService;
import com.example.smstest.domain.file.MD5Generator;
import com.example.smstest.domain.project.ProjectRepository;
import com.example.smstest.domain.task.dto.TaskRequestDTO;
import com.example.smstest.domain.task.entity.Task;
import com.example.smstest.domain.task.repository.TaskCategoryRepository;
import com.example.smstest.domain.task.repository.TaskRepository;
import com.example.smstest.global.exception.CustomException;
import com.example.smstest.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 일정 관련 Service
 */
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskCategoryRepository taskCategoryRepository;
    private final ProjectRepository projectRepository;
    private final FileService fileService;

    /**
     * 일정 삭제
     * @param taskId
     * @return
     */
    public String deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        taskRepository.delete(task);
        return "Task deleted!";
    }

    /**
     * 기존 일정 업데이트
     * @param taskId
     * @param json
     * @param file
     * @return
     */
    public String updateTask(Long taskId, Map<String, Object> json,
                             MultipartFile file) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        setTaskRequestDto(json, file, task);
        return "Task updated!";
    }

    /**
     * 신규 일정 등록
     * @param projectId
     * @param json
     * @param file
     * @return
     */
    public String saveTask(Long projectId, Map<String, Object> json,
                            MultipartFile file) {

        ObjectMapper mapper = new ObjectMapper();
        TaskRequestDTO taskRequest = mapper.convertValue(json,TaskRequestDTO.class);

        Task task = new Task();
        task.setProject(projectRepository.findById(projectId).get());
        task.setCategory(taskCategoryRepository.findById(taskRequest.getCategoryId()).get());
        task.setTaskName(taskRequest.getTaskName());
        task.setEstimatedStartDate(taskRequest.getEstimatedStartDate());
        task.setEstimatedEndDate(taskRequest.getEstimatedEndDate());
        task.setActualStartDate(taskRequest.getActualStartDate());
        task.setActualEndDate(taskRequest.getActualEndDate());
        Task newTask = taskRepository.save(task);

        return setTaskFile(file, newTask);
    }

    /**
     * json으로 받은 Request를 TaskRequestDto로 매핑
     * @param json
     * @param file
     * @param task
     * @return
     */
    private String setTaskRequestDto(Map<String, Object> json,
                                     MultipartFile file, Task task) {

        ObjectMapper mapper = new ObjectMapper();
        TaskRequestDTO updatedTask = mapper.convertValue(json,TaskRequestDTO.class);

        task.setCategory(taskCategoryRepository.findById(updatedTask.getCategoryId()).get());
        task.setTaskName(updatedTask.getTaskName());
        task.setEstimatedStartDate(updatedTask.getEstimatedStartDate());
        task.setEstimatedEndDate(updatedTask.getEstimatedEndDate());
        task.setActualStartDate(updatedTask.getActualStartDate());
        task.setActualEndDate(updatedTask.getActualEndDate());

        if (updatedTask.getFileDeleted()){
            task.setFiles(null);
            fileService.deleteAllByTaskId(task.getId());
        }

        Task newTask = taskRepository.save(task);

        return setTaskFile(file, newTask);
    }

    /**
     * 요청 들어온 file 저장
     * @param file
     * @param newTask
     * @return
     */
    private String setTaskFile(MultipartFile file, Task newTask){

        // 요청 들어온 file이 없을 경우 그냥 return
        if (file == null){
            return "작업 목록이 성공적으로 저장되었습니다.";
        }

        // file이 있을 경우 저장
        try {

            if (file.getOriginalFilename().isEmpty())
                throw new CustomException(ErrorCode.BAD_REQUEST);

            String origFilename = file.getOriginalFilename();
            String filename = new MD5Generator(origFilename).toString();

            String savePath;
            String filePath;

            // OS 따라 구분자 분리
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")){
                savePath = System.getProperty("user.dir") + "\\files\\task";
                filePath = savePath + "\\" + filename;
            }
            else{
                savePath = System.getProperty("user.dir") + "/files/task";
                filePath = savePath + "/" + filename;
            }


            if (!new java.io.File(savePath).exists()) {
                try{
                    new java.io.File(savePath).mkdir();
                }
                catch(Exception e){
                    e.getStackTrace();
                }
            }

            file.transferTo(new java.io.File(filePath));
            
            // 이전에 등록되어 있던 File 삭제
            fileService.deleteAllByTaskId(newTask.getId());

            FileDto fileDto = new FileDto();
            fileDto.setOrigFilename(origFilename);
            fileDto.setSize(file.getSize());
            fileDto.setFilename(filename);
            fileDto.setFilePath(filePath);
            fileDto.setTaskId(newTask.getId());

            // 신규 등록 File 저장
            fileService.saveFile(fileDto);


            return "작업 목록이 성공적으로 저장되었습니다.";

        } catch(Exception e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.BAD_REQUEST);

        }
    }

    public void generateExcel(Long projectId, HttpServletResponse response) throws IOException, DocumentException {
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
