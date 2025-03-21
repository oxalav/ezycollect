package com.ezycollect.demo.repository;

import com.ezycollect.demo.dto.PaymentDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext
class PaymentRepositoryComponentTest {
    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    @DirtiesContext
    public void shouldSavePaymentEvent() {
        PaymentDTO payment = new PaymentDTO();

        payment.setFirstName("Tony");
        payment.setLastName("Stark");
        payment.setZipCode("3000");
        payment.setCardNumber("3456789034567890");
        payment.setAmount(12345);

        Integer id = paymentRepository.save(payment).getId();

        PaymentDTO savedPayment = paymentRepository.findById(id).orElseThrow();

        assertThat(savedPayment.getFirstName()).isEqualTo("Tony");
        assertThat(savedPayment.getLastName()).isEqualTo("Stark");
        assertThat(savedPayment.getZipCode()).isEqualTo("3000");
        assertThat(savedPayment.getCardNumber()).isEqualTo("3456789034567890");
        assertThat(savedPayment.getAmount()).isEqualTo(12345);
    }
}

