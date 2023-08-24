package com.example.smstest.scheduler;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RequiredArgsConstructor
@Component

public class GeoTranslate {
    private final JdbcTemplate jdbcTemplate;
    static List<String> customerNames;

    @Scheduled(fixedDelay = 1000000000)
    public void main() {
        String sql = "SELECT DISTINCT \"고객사\" FROM customer";
        customerNames = jdbcTemplate.queryForList(sql, String.class);
        String apiKey = "AIzaSyAgw5WXDsG64H0k6sKmaahRlQntUiscOoU";
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();

        for (String customerName : customerNames) {
            try {
                GeocodingResult[] results = GeocodingApi.geocode(context, customerName).await();

                if (results.length > 0) {
                    double latitude = results[0].geometry.location.lat;
                    double longitude = results[0].geometry.location.lng;
                    System.out.println("Latitude: " + latitude + ", Longitude: " + longitude);
                } else {
                    System.out.println("No results found.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}



