package com.example.smstest.domain.support.scheduler;

import com.example.smstest.domain.support.repository.SupportRepository;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DriveQuickstart {

    final GoogleSheets googleSheets;
    static final String FOLDER_ID = "1eqGzcFtgydwqGp5u0d1txSRQShoZ0JAL";
    static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens_drive";
    private static final List<String> SCOPES =
            Collections.singletonList(DriveScopes.DRIVE_METADATA_READONLY);
    final SupportRepository supportRepository;
    private static final String CREDENTIALS_FILE_PATH = "/token/service_key.json";
    private final JdbcTemplate jdbcTemplate;

    static int driveCnt = 0;

//    @Scheduled(fixedDelay = 1000000000)
    public void scheduleGetInitialFiles() throws InterruptedException, GeneralSecurityException, IOException {
        getFiles(FOLDER_ID);
    }

    @Async
    Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load service account credentials from JSON file
        InputStream in = FetchChanges.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }

        GoogleCredential credential = GoogleCredential.fromStream(in, HTTP_TRANSPORT, JSON_FACTORY)
                .createScoped(SCOPES);

        return credential;
    }


    @Async
    public void getFiles(String folderId) throws GeneralSecurityException, IOException, InterruptedException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        String nextPageToken = null;
        do {
            FileList result = service.files().list()
                    .setQ("'" + folderId + "' in parents")
                    .setPageSize(100)
                    .setFields("nextPageToken, files")
                    .setPageToken(nextPageToken)
                    .execute();

            List<File> files = result.getFiles();
            if (files == null || files.isEmpty()) {
                System.out.println("No files found.");
            } else {
                System.out.println("======= Files in Folder ("+folderId+")==========");

                for (File file : files) {
                    if (file.getMimeType().equals("application/vnd.google-apps.folder")){
                        getFiles(file.getId());
                        System.out.println(file.getName() +  " (" + file.getMimeType()+ ") : "+ file.getWebViewLink());
                    }
                    else if (file.getMimeType().equals("application/vnd.google-apps.spreadsheet")){
                        driveCnt++;
                        System.out.println("driveCnt : "+ driveCnt);
                        googleSheets.insertFile(file.getId(), file.getName(), file.getWebViewLink());

                    }
                }
            }
            nextPageToken = result.getNextPageToken();
        } while (nextPageToken != null);
    }

    @Async("threadPoolTaskExecutor")
    public void insertFile(String spreadsheetId, String fileName, String fileLink) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        System.out.println("\n"+spreadsheetId+"\n");

        Sheets service = new Sheets.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        BatchGetValuesResponse result = null;

        try {

            Map<String, String> allMappings = null;
            ValueRange is_title = null;
            try{
                is_title = service.spreadsheets().values()
                        .get(spreadsheetId, "B6")
                        .execute();
            }
            catch(GoogleJsonResponseException e){
                // 503 Service Unavailable
                Thread.sleep(3000);
                is_title = service.spreadsheets().values()
                        .get(spreadsheetId, "B6")
                        .execute();
            }


            String b6Value = "";
            if (is_title.getValues()!=null){
                b6Value = (String) is_title.getValues().get(0).get(0);
            }

            if (b6Value.trim().equals("작업 제목")){
                allMappings = getAllMappings_new();
            }
            else{
                allMappings = getAllMappings();
            }

            Map<String, Object> data = new HashMap<>();

            List<String> keys =  new ArrayList<>();

            List<String> ranges = new ArrayList<>();

            for (Map.Entry<String, String> entry : allMappings.entrySet()) {
                String value = entry.getValue();
                ranges.add(value);
                keys.add(entry.getKey());
            }

            try {
                result = service.spreadsheets().values()
                        .batchGet(spreadsheetId)
                        .setRanges(ranges)
                        .execute();

            }
            catch (GoogleJsonResponseException e) {
                GoogleJsonError error = e.getDetails();
                if (error.getCode() == 404) {
                    System.out.printf("Spreadsheet not found with id '%s'.\n", spreadsheetId);
                    return;
                }
                else if (error.getCode() == 500 || error.getCode() == 503) {
                    System.out.printf("Internel Server Error... Wait a mement...");
                    Thread.sleep(1000);
                    result =  service.spreadsheets().values()
                            .batchGet(spreadsheetId)
                            .setRanges(ranges)
                            .execute();
                }
                else if(error.getCode()==429){
                    SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
                    System.out.println("\nToo Many Requests... Wait 100 seconds, Now is " +format1.format (System.currentTimeMillis()));
                    Thread.sleep(100000);
                    result = service.spreadsheets().values()
                            .batchGet(spreadsheetId)
                            .setRanges(ranges)
                            .execute();
                }
                else {
                    throw e;
                }
            }
            List<ValueRange> cellValues = result.getValueRanges();

            Object cellValue;

            for (int i =0; i<cellValues.size(); i++){

                cellValue = cellValues.get(i).getValues();

                while (cellValue instanceof ArrayList) {

                    if (((ArrayList<?>) cellValue).size()>0)
                        cellValue = ((ArrayList<?>) cellValue).get(0);
                    else
                        break;

                }
                data.put(keys.get(i), cellValue != null ? cellValue : "");
                Thread.sleep(1000);

            }

            // 작업 요약과 작업 세부내역 모두 공백이면 데이터 입력 안함
            if (data.get("작업요약").equals("") && data.get("작업세부내역").equals("")){
                System.out.println(fileName+"  (" +fileLink + ") is invalid format");
                return;
            }
            else {
                // 지원일자 date 포맷팅
                SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy.MM.dd");
                // 문자열 -> Date
                Date date = null;
                try {
                    // "yyyy-MM-dd" 형식으로 파싱을 시도합니다.
                    date = formatter1.parse((String) data.get("지원일자"));
                } catch (ParseException e1) {
                    // "yyyy-MM-dd" 형식으로 파싱이 실패한 경우 "yyyy.MM.dd" 형식으로 파싱을 시도합니다.
                    try {
                        date = formatter2.parse((String) data.get("지원일자"));
                    } catch (ParseException e2) {
                        System.err.println("\n날짜 파싱 오류: " + e2.getMessage());
                    }
                }


                data.put("파일ID", spreadsheetId);
                data.put("파일명", fileName);
                data.put("위치", fileLink);

                // 엔지니어 앞 3글자까지만 자르고 공백 없애기
                String engineer = (String) data.get("담당엔지니어");
                String truncatedEngineer = engineer.substring(0, Math.min(3, engineer.length())).replace(" ", "");


                try{
                    // Support 엔티티 저장
                    String sql = "INSERT INTO support (\"파일id\", \"제품명\", \"고객사\", \"고객담당자\", \"작업구분\", \"이슈구분\", \"업무구분\", \"담당엔지니어\", \"지원일자\", \"지원형태\", \"레드마인_일감\", \"작업제목\", \"작업요약\", \"작업세부내역\", \"파일명\", \"위치\") " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                    jdbcTemplate.update(sql,
                            data.get("파일ID"),
                            data.get("제품명"),
                            data.get("고객사"),
                            data.get("고객담당자"),
                            data.get("작업구분"),
                            data.get("이슈구분"),
                            data.get("업무구분"),
                            truncatedEngineer,
                            date,
                            data.get("지원형태"),
                            data.get("레드마인_일감"),
                            data.get("작업제목"),
                            data.get("작업요약"),
                            data.get("작업세부내역"),
                            data.get("파일명"),
                            data.get("위치")
                    );

                    System.out.println("\nInsert 성공!! " + data);
                }
                catch (DuplicateKeyException e) {
                    System.out.println("중복된 key값, PASS ("+data.get("파일ID")+")");
                }
                catch (Exception e){
                    System.out.println("Error : "+e.getMessage()+", PASS ("+data.get("파일ID")+")");

                }
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Async("threadPoolTaskExecutor")
    public Map<String, String> getAllMappings() {
        String sql = "SELECT key_column, value_column FROM key_value_mapping";
        Map<String, String> mappings = new HashMap<>();

        try {
            jdbcTemplate.query(sql, (resultSet) -> {
                while (resultSet.next()) {
                    String key = resultSet.getString("key_column");
                    String value = resultSet.getString("value_column");
                    mappings.put(key, value);
                }
                return mappings;
            });
        } catch (Exception e) {
            System.err.println("\nError reading data: " + e.getMessage());
        }

        return mappings;
    }

    @Async("threadPoolTaskExecutor")
    public Map<String, String> getAllMappings_new() {
        String sql = "SELECT key_column, value_column FROM key_value_mapping_new";
        Map<String, String> mappings = new HashMap<>();

        try {
            jdbcTemplate.query(sql, (resultSet) -> {
                while (resultSet.next()) {
                    String key = resultSet.getString("key_column");
                    String value = resultSet.getString("value_column");
                    mappings.put(key, value);
                }
                return mappings;
            });
        } catch (Exception e) {
            System.err.println("\nError reading data: " + e.getMessage());
        }

        return mappings;
    }
}