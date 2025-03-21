package com.ezycollect.demo.controller;

import com.ezycollect.demo.dto.NotificationDTO;
import com.ezycollect.demo.model.Payment;
import com.ezycollect.demo.repository.NotificationRepository;
import com.ezycollect.demo.repository.PaymentRepository;
import com.ezycollect.demo.repository.WebHookRepository;
import com.ezycollect.demo.service.EncryptionService;
import com.ezycollect.demo.service.NotificationService;
import com.ezycollect.demo.util.ConvertUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.Instant;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "/api/v1/payment")
@CrossOrigin(origins = "*")
public class PaymentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private WebHookRepository webHookRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private EncryptionService encryptionService;
    @Autowired
    private NotificationService notificationService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public @ResponseBody ResponseEntity<String> create(@Valid @RequestBody Payment payment, BindingResult bindingResult) {
        LOGGER.info("Processing new payment: {}", payment);

        if (bindingResult.hasErrors()) {
            LOGGER.info("Invalid payment rejected: {}", bindingResult.getAllErrors());
            return ResponseEntity.badRequest()
                    .body("Invalid fields: " + bindingResult.getFieldErrors().stream().map(FieldError::getField).collect(Collectors.joining(", ")));
        }

        createNotifications(createPayment(payment));

        LOGGER.info("Payment successfully processed");
        return ResponseEntity.ok().body("Saved");
    }

    private Integer createPayment(Payment payment) {
        LOGGER.info("Saving payment to the database");
        return paymentRepository.save(ConvertUtil.paymentToPaymentDto(payment, encryptionService)).getId();
    }

    private void createNotifications(Integer paymentId) {
        webHookRepository.findAll().forEach(webHookDto -> {
            NotificationDTO notificationDto = new NotificationDTO();

            notificationDto.setPaymentId(paymentId);
            notificationDto.setUrl(webHookDto.getUrl());
            notificationDto.setRetries(0);
            notificationDto.setCreateTimestamp(Instant.now());

            LOGGER.info("Creating Web hook notification");
            notificationRepository.save(notificationDto);
        });
        notificationService.processNotifications();
    }
}
