package com.example.smstest.domain.support.scheduler;

import com.example.smstest.domain.support.repository.SupportRepository;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.SocketTimeoutException;
import java.security.GeneralSecurityException;
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
    List<String> lists;
    static int driveCnt = 0;

//    @Scheduled(fixedDelay = 1000000000)
//    @Scheduled(cron = "0 0 6 * * *") // 매일 06시 실행
    public void scheduleGetInitialFiles() throws InterruptedException, GeneralSecurityException, IOException {
        String sql = "SELECT DISTINCT \"파일id\" FROM support";
        lists = jdbcTemplate.queryForList(sql, String.class);
        Thread.sleep(2000);
        System.out.println(lists);
        getFiles(FOLDER_ID);

    }

    @Async
    Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load service account credentials from JSON file
        InputStream in = DriveQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }

        GoogleCredential credential = GoogleCredential.fromStream(in, HTTP_TRANSPORT, JSON_FACTORY)
                .createScoped(SCOPES);

        return credential;
    }


    @Async
    public void getFiles(String folderId) throws GeneralSecurityException, IOException, InterruptedException {

        String nextPageToken = null;
        do {
            try{
                nextPageToken = getFiles(folderId, nextPageToken);
            }
            catch(SocketTimeoutException ex){ // Read timed out
                Thread.sleep(3000);
                nextPageToken = getFiles(folderId, nextPageToken);
            }
            catch (Exception ex){
                log.error(ex.getMessage() + " folderId : " + folderId + " nextPageToken : "+nextPageToken);
            }


        } while (nextPageToken != null);
    }

    public String getFiles(String folderId, String nextPageToken) throws GeneralSecurityException, IOException, InterruptedException {

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();


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
                else if (file.getMimeType().equals("application/vnd.google-apps.spreadsheet") && !lists.contains(file.getId())){
                    driveCnt++;
                    System.out.println("driveCnt : "+ driveCnt+ " " + file.getName() +  " (" + file.getMimeType()+ ") : "+ file.getWebViewLink());
                    googleSheets.insertFile(file.getId(), file.getName(), file.getWebViewLink());

                }
            }
        }
        nextPageToken = result.getNextPageToken();

        return nextPageToken;
    }


}