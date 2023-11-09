package com.example.smstest.domain.file;

import com.example.smstest.exception.CustomException;
import com.example.smstest.exception.ErrorCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;


@RestController
@RequestMapping("/file")
public class FileRestController {

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadTestPOST(MultipartFile[] uploadFile) {

        // 내가 업로드 파일을 저장할 경로

        /* 실행되는 위치의 'files' 폴더에 파일이 저장됩니다. */
        String savePath;

        // OS 따라 구분자 분리
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")){
            savePath = System.getProperty("user.dir") + "\\files\\image";
        }
        else{
            savePath = System.getProperty("user.dir") + "/files/image";
        }

//        SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd");
//        Date date = new Date();
//        String formatDate = sdt.format(date);
//
//        String datePath = formatDate.replace("-", java.io.File.separator);

        java.io.File uploadPath = new java.io.File(savePath);

        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }

        for (MultipartFile multipartFile : uploadFile) {

            String uploadFileName = multipartFile.getOriginalFilename();

            /* 변경 위치 ............. */
            String uuid = UUID.randomUUID().toString();
            uploadFileName = uuid + "_" + uploadFileName;

            java.io.File saveFile = new java.io.File(uploadPath, uploadFileName);

            try {
                multipartFile.transferTo(saveFile);
                return uploadFileName;
            } catch (Exception e) {
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }
        }
        return savePath;
    }

    @ResponseBody
    @GetMapping(value = "/display")
    public ResponseEntity<byte[]> showImageGET(
            @RequestParam("fileName") String fileName
    ) {

        String savePath;

        // OS 따라 구분자 분리
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")){
            savePath = System.getProperty("user.dir") + "\\files\\image\\";
        }
        else{
            savePath = System.getProperty("user.dir") + "/files/image/";
        }
        java.io.File file = new java.io.File(savePath + fileName);

        ResponseEntity<byte[]> result = null;

        try {

            HttpHeaders header = new HttpHeaders();
            header.add("Content-type", Files.probeContentType(file.toPath()));

            result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

}
