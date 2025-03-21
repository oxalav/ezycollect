package com.ezycollect.demo.util;

import com.ezycollect.demo.dto.PaymentDTO;
import com.ezycollect.demo.model.Payment;
import com.ezycollect.demo.service.DecryptionService;
import com.ezycollect.demo.service.EncryptionService;

public class ConvertUtil {
    public static PaymentDTO paymentToPaymentDto(Payment payment, EncryptionService encryptionService) {
        PaymentDTO paymentDto = new PaymentDTO();

        paymentDto.setFirstName(payment.getFirstName());
        paymentDto.setLastName(payment.getLastName());
        paymentDto.setZipCode(payment.getZipCode());
        paymentDto.setCardNumber(encryptionService.encrypt(payment.getCardNumber()));
        paymentDto.setAmount(payment.getAmount());

        return paymentDto;
    }

    public static Payment paymentDtoToPayment(PaymentDTO paymentDto, DecryptionService decryptionService) {
        Payment payment = new Payment();

        payment.setFirstName(paymentDto.getFirstName());
        payment.setLastName(paymentDto.getLastName());
        payment.setZipCode(paymentDto.getZipCode());
        payment.setCardNumber(decryptionService.decrypt(paymentDto.getCardNumber()));
        payment.setAmount(paymentDto.getAmount());

        return payment;
    }
}
