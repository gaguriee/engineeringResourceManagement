package com.example.smstest.domain.support.scheduler;

import com.example.smstest.domain.support.repository.SupportRepository;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

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
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    final SupportRepository supportRepository;
    static int driveCnt = 0;

//    @Scheduled(fixedDelay = 1000000000)
//    public void scheduleGetInitialFiles() throws InterruptedException, GeneralSecurityException, IOException {
//        getFiles(FOLDER_ID);
//    }

    @Async
    Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
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
}