package com.ddvs.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationClient {

    private final RestTemplate restTemplate;

    @Value("${notification.service.url}")
    private String notificationServiceUrl;

    public void send(String recipient, String subject,
                     String templateName, Map<String, Object> data) {
        try {
            Map<String, Object> request = Map.of(
                    "type", "EMAIL",
                    "recipient", recipient,
                    "subject", subject,
                    "templateName", templateName,
                    "data", data
            );

            restTemplate.postForEntity(
                    notificationServiceUrl + "/notifications",
                    request,
                    Object.class
            );

            log.info("Notification sent to {}", recipient);

        } catch (Exception e) {
            // Never let notification failure break the main flow
            log.error("Notification failed for {} | {}", recipient, e.getMessage());
        }
    }
}