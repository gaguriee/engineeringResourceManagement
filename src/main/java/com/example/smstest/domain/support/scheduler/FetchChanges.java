package com.example.smstest.domain.support.scheduler;

import com.example.smstest.domain.support.repository.SupportRepository;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.ChangeList;
import com.google.api.services.drive.model.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class FetchChanges {

    private final JdbcTemplate jdbcTemplate;

    final GoogleSheets googleSheets;
    static final String APPLICATION_NAME = "Google Drive API Change Detect";
    static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens_drive";
    private static final List<String> SCOPES =
            Collections.singletonList(DriveScopes.DRIVE_METADATA_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    final SupportRepository supportRepository;

//    @Scheduled(fixedDelay = 1000000000)
    @Scheduled(cron = "0 0 0,6,12,18 * * ?") // 매일 06시, 12시, 18시, 24시 실행
    public void fetchChanges() throws InterruptedException, IOException, GeneralSecurityException {

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        HttpRequestInitializer requestInitializer = getCredentials(HTTP_TRANSPORT);

        Drive service = new Drive.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName(APPLICATION_NAME)
                .build();

        String savedStartPageToken = getStartPageToken();

        try {
            String pageToken = savedStartPageToken;

            // 현재 날짜 구하기 (시스템 시계, 시스템 타임존)
            LocalDateTime now = LocalDateTime.now();
            System.out.println("\n\nScheduled in "+now);

            System.out.println("BEFORE startpageToken : " + pageToken);
            while (pageToken != null) {
                ChangeList changes = service.changes().list(pageToken)
                        .execute();
                for (com.google.api.services.drive.model.Change change : changes.getChanges()) {
                    // Process change
                    System.out.println("\nChange found for file: " + change);
                    File file = change.getFile();
                    if (file == null || change.getRemoved()){ // 삭제된 파일 예외처리
                        break;
                    }
                    if (file.getMimeType().equals("application/vnd.google-apps.spreadsheet")) {

                        // WebViewLink를 반환하지 않는 detect api를 위해 webviewlink만 가져옴
                        File file_ = service.files().get(file.getId()).setFields("name, webViewLink").execute();
                        String webViewLink = file_.getWebViewLink();

                        System.out.println(file.getName() + " (" + file.getMimeType() + ") : " + webViewLink);

                        googleSheets.insertFile(file.getId(), file.getName(), webViewLink);
                    }
                }
                if (changes.getNewStartPageToken() != null) {
                    // Last page, save this token for the next polling interval
                    savedStartPageToken = changes.getNewStartPageToken();
                }
                pageToken = changes.getNextPageToken();
            }

            saveStartPageToken(savedStartPageToken);
            System.out.println("\nAFTER startpageToken : " + savedStartPageToken);

        } catch (GoogleJsonResponseException e) {
            System.err.println("\nUnable to fetch changes: " + e.getDetails());
            throw e;
        }

    }

    @Async
    Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = DriveQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        return credential;
    }

    private String getStartPageToken() {
        String sql = "SELECT token_value FROM token WHERE token_name = 'start_page_token'";
        try {
            return jdbcTemplate.queryForObject(sql, String.class);
        } catch (Exception e) {
            // 오류 처리
            System.err.println("Error getting start_page_token value: " + e.getMessage());
            return null;
        }
    }

    public boolean saveStartPageToken(String savedStartPageToken) {
        String sql = "UPDATE token SET token_value = ?, updated_at = ? WHERE token_name = 'start_page_token'";
        try {
            int rowsUpdated = jdbcTemplate.update(sql, savedStartPageToken, LocalDateTime.now());
            String sql2 = "INSERT INTO token_history (\"token_name\", \"token_value\", \"updated_at\") " +
                    "VALUES (?, ?, ?)";
            jdbcTemplate.update(sql2,"start_page_token", savedStartPageToken, LocalDateTime.now());
            return rowsUpdated > 0;
        } catch (Exception e) {
            // 오류 처리
            System.err.println("Error updating start_page_token value: " + e.getMessage());
            return false;
        }
    }

}