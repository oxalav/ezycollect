package com.ezycollect.demo.service;

import com.ezycollect.demo.model.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext
class WebHookCallServiceComponentTest {
    @LocalServerPort
    private int port;
    @Autowired
    private WebHookCallService webHookCallService;

    @Test
    public void shouldCallWebHookWithSuccess() {
        webHookCallService.callWebHook("http://localhost:" + port + "/webhook/success", createPayment());
    }

    @Test
    public void shouldCallWebHookWithFailure() {
        assertThrows(HttpClientErrorException.BadRequest.class,
                () -> webHookCallService.callWebHook("http://localhost:" + port + "/webhook/failure", createPayment()));
    }

    @Test
    public void shouldCallIncorrectWebHook() {
        assertThrows(HttpClientErrorException.NotFound.class,
                () -> webHookCallService.callWebHook("http://localhost:" + port + "/webhook/unknown", createPayment()));
    }

    private static Payment createPayment() {
        Payment payment = new Payment();

        payment.setFirstName("Tony");
        payment.setLastName("Stark");
        payment.setZipCode("3000");
        payment.setCardNumber("3456789034567890");
        payment.setAmount(12345);

        return payment;
    }

    @TestConfiguration
    public static class TestConfig {
        @Controller
        @RequestMapping("/webhook")
        public class TestController {
            @PostMapping("/success")
            public @ResponseBody ResponseEntity<String> success(@RequestBody Payment payment) {
                assertThat(payment).isEqualTo(createPayment());
                return ResponseEntity.ok().body("success");
            }

            @PostMapping("/failure")
            public @ResponseBody ResponseEntity<String> failure(@RequestBody Payment payment) {
                assertThat(payment).isEqualTo(createPayment());
                return ResponseEntity.badRequest().body("failure");
            }
        }
    }
}
