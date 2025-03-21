package com.ezycollect.demo.service;

import com.ezycollect.demo.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WebHookCallServiceServiceImpl implements WebHookCallService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebHookCallServiceServiceImpl.class);

    private final RestClient restClient;

    public WebHookCallServiceServiceImpl(@Autowired RestClient.Builder builder) {
        restClient = builder.build();
    }

    @Override
    public void callWebHook(String url, Payment payment) {
        LOGGER.info("Calling Web hook {} with payload {}", url, payment);

        ResponseEntity<Void> response = restClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payment)
                .retrieve()
                .toBodilessEntity();

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("Web hook failed with status " + response.getStatusCode());
        }
    }
}
