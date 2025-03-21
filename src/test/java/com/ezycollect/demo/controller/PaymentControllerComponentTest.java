package com.ezycollect.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ezycollect.demo.model.Payment;
import com.ezycollect.demo.repository.NotificationRepository;
import com.ezycollect.demo.repository.PaymentRepository;
import com.ezycollect.demo.service.NotificationService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext
class PaymentControllerComponentTest {
    private static final String API_PAYMENT_PATH = "/api/v1/payment";
    private static final String API_WEBHOOK_REGISTER_PATH = "/api/v1/webhook/register";
    private static final String WEBHOOK_URL_1 = "http://localhost:12345/callback1";
    private static final String WEBHOOK_URL_2 = "http://localhost:12345/callback2";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private NotificationService mockNotificationService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    @BeforeEach
    public void registerWebHooks() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(API_WEBHOOK_REGISTER_PATH).param("url", WEBHOOK_URL_1))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.post(API_WEBHOOK_REGISTER_PATH).param("url", WEBHOOK_URL_2))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DirtiesContext
    public void shouldRejectInvalidField() throws Exception {
        Payment payment = new Payment();

        payment.setFirstName("Tony");
        payment.setLastName("Stark");
        payment.setZipCode("3000");
        payment.setCardNumber("12345");
        payment.setAmount(12345);

        mockMvc.perform(MockMvcRequestBuilders.post(API_PAYMENT_PATH)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(payment)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("cardNumber")));

        assertThat(paymentRepository.findAll()).noneMatch(paymentDto -> Objects.equals(payment.getFirstName(), paymentDto.getFirstName()));
        verify(mockNotificationService, never()).processNotifications();
    }

    @Test
    @DirtiesContext
    public void shouldCreatePaymentAndNotifications() throws Exception {
        Payment payment = new Payment();

        payment.setFirstName("Tony");
        payment.setLastName("Stark");
        payment.setZipCode("3000");
        payment.setCardNumber("3456789034567890");
        payment.setAmount(12345);

        mockMvc.perform(MockMvcRequestBuilders.post(API_PAYMENT_PATH)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(payment)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Saved"));

        verifyPaymentCreated(payment);
        verifyNotificationsCreated();
    }

    private void verifyPaymentCreated(Payment payment) {
        assertThat(paymentRepository.findAll()).anyMatch(
                paymentDto -> Objects.equals(payment.getFirstName(), paymentDto.getFirstName())
                        && Objects.equals(payment.getLastName(), paymentDto.getLastName())
                        && Objects.equals(payment.getZipCode(), paymentDto.getZipCode())
                        && Objects.equals(payment.getAmount(), paymentDto.getAmount()));
    }

    private void verifyNotificationsCreated() {
        assertThat(notificationRepository.findAll()).anyMatch(
                notificationDto -> Objects.equals(notificationDto.getUrl(), WEBHOOK_URL_1));
        assertThat(notificationRepository.findAll()).anyMatch(
                notificationDto -> Objects.equals(notificationDto.getUrl(), WEBHOOK_URL_2));
        verify(mockNotificationService, times(1)).processNotifications();
    }
}
