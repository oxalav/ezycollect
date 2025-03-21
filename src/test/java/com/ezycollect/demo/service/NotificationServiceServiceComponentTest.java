package com.ezycollect.demo.service;

import com.ezycollect.demo.dto.NotificationDTO;
import com.ezycollect.demo.model.Payment;
import com.ezycollect.demo.repository.NotificationRepository;
import com.ezycollect.demo.repository.PaymentRepository;
import com.ezycollect.demo.util.ConvertUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext
class NotificationServiceServiceComponentTest {
    private static final String WEBHOOK_URL_1 = "http://localhost:12345/callback1";
    private static final String WEBHOOK_URL_2 = "http://localhost:12345/callback2";

    @MockitoBean
    private WebHookCallService mockWebHookCallService;

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private EncryptionService encryptionService;
    @Autowired
    private NotificationServiceImpl notificationProcessorService;

    @BeforeEach
    public void createNotifications() {
        int paymentId = createPayment();
        createNotification(paymentId, WEBHOOK_URL_1);
        createNotification(paymentId, WEBHOOK_URL_2);

        assertThat(notificationRepository.findAll()).hasSize(2);
    }

    @Test
    @DirtiesContext
    public void shouldProcessNotifications() throws Exception {
        CompletableFuture<?> future1 = new CompletableFuture<>();
        doAnswer(invocation -> future1.complete(null))
                .when(mockWebHookCallService).callWebHook(eq(WEBHOOK_URL_1), any());

        CompletableFuture<?> future2 = new CompletableFuture<>();
        doAnswer(invocation -> future2.complete(null))
                .when(mockWebHookCallService).callWebHook(eq(WEBHOOK_URL_2), any());

        notificationProcessorService.processNotifications();

        future1.get(10, TimeUnit.SECONDS);
        future2.get(10, TimeUnit.SECONDS);
    }

    @Test
    @DirtiesContext
    public void shouldRetryFailedNotifications() throws Exception {
        CompletableFuture<?> future1 = new CompletableFuture<>();
        doAnswer(invocation -> future1.complete(null))
                .when(mockWebHookCallService).callWebHook(eq(WEBHOOK_URL_1), any());

        AtomicInteger errorCounter = new AtomicInteger(2);
        CompletableFuture<?> future2 = new CompletableFuture<>();
        doAnswer(invocation -> {
            if (errorCounter.decrementAndGet() > 0) {
                throw new IllegalStateException();
            }
            return future2.complete(null);
        }).when(mockWebHookCallService).callWebHook(eq(WEBHOOK_URL_2), any());

        notificationProcessorService.processNotifications();
        future1.get(10, TimeUnit.SECONDS);

        notificationProcessorService.processNotifications();
        future2.get(10, TimeUnit.SECONDS);

        assertThat(errorCounter).hasValue(0);
    }

    private int createPayment() {
        Payment payment = new Payment();

        payment.setFirstName("Tony");
        payment.setLastName("Stark");
        payment.setZipCode("3000");
        payment.setCardNumber("3456789034567890");
        payment.setAmount(12345);

        return paymentRepository.save(ConvertUtil.paymentToPaymentDto(payment, encryptionService)).getId();
    }

    private void createNotification(int paymentId, String url) {
        NotificationDTO notificationDto = new NotificationDTO();

        notificationDto.setPaymentId(paymentId);
        notificationDto.setUrl(url);
        notificationDto.setCreateTimestamp(Instant.now());
        notificationDto.setRetries(0);

        notificationRepository.save(notificationDto);
    }
}
