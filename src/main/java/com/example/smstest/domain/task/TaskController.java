package com.example.smstest.domain.task;

import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * WBS 작업 일정에 관련된 Controller
 * 작업 일정 생성, 업데이트, 삭제
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    /**
     * 일정 삭제
     * @param taskId
     * @return
     */
    @PostMapping("/delete/{taskId}")
    @ResponseBody
    public String deleteTask(@PathVariable Long taskId) {
        return taskService.deleteTask(taskId);
    }

    /**
     * 기존 일정 수정 (파일 첨부)
     * @param taskId
     * @param json
     * @param file
     * @return
     */
    @PostMapping("/update/{taskId}")
    @ResponseBody
    public String updateTask(@PathVariable Long taskId,@RequestPart Map<String, Object> json,
                             @RequestPart(required = false) MultipartFile file) {
        return taskService.updateTask(taskId, json, file);
    }

    /**
     * 신규 일정 등록 (파일 첨부)
     * @param projectId
     * @param json
     * @param file
     * @return
     */
    @PostMapping("/save/{projectId}")
    @ResponseBody
    public String saveTasks(@PathVariable Long projectId, @RequestPart Map<String, Object> json,
                            @RequestPart(required = false) MultipartFile file) {
        return taskService.saveTask(projectId, json, file);
    }

    /**
     * WBS 작업 히스토리 다운로드
     * @param projectId
     * @param response
     * @throws IOException
     * @throws DocumentException
     */
    @GetMapping("/generateExcel")
    public void generateProjectExcel(@RequestParam(required = false) Long projectId, HttpServletResponse response) throws DocumentException, IOException {

        taskService.generateExcel(projectId, response);

    }

}
