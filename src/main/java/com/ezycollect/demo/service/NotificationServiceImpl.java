package com.ezycollect.demo.service;

import com.ezycollect.demo.dto.NotificationDTO;
import com.ezycollect.demo.dto.PaymentDTO;
import com.ezycollect.demo.repository.NotificationRepository;
import com.ezycollect.demo.repository.PaymentRepository;
import com.ezycollect.demo.util.ConvertUtil;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

import static java.lang.Math.max;

@Service
public class NotificationServiceImpl implements NotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final Object lock = new Object();

    private int latestKnownId = -1;

    @Value("${application.notification.retry.seconds:10}")
    private long retrySeconds;
    @Value("${application.notification.max.retries:3}")
    private int maxRetries;

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private DecryptionService decryptionService;
    @Autowired
    private WebHookCallService webHookCallService;
    @Autowired
    private TaskScheduler taskScheduler;

    @PostConstruct
    @Async
    @Transactional
    @Override
    public void processNotifications() {
        LOGGER.info("Processing outstanding Web hook notifications");

        synchronized (lock) {
            notificationRepository.findUnprocessed(latestKnownId).forEach(this::scheduleNotification);
        }
    }

    private void scheduleNotification(NotificationDTO notificationDto) {
        latestKnownId = max(latestKnownId, notificationDto.getId());
        taskScheduler.schedule(() -> processNotification(notificationDto), Instant.now());
    }

    private void processNotification(NotificationDTO notificationDto) {
        try {
            LOGGER.info("Processing notification {}", notificationDto);

            Optional<PaymentDTO> maybePayment = paymentRepository.findById(notificationDto.getPaymentId());
            if (maybePayment.isEmpty()) {
                LOGGER.error("Payment {} not found, ignoring", notificationDto.getPaymentId());
            } else {
                webHookCallService.callWebHook(notificationDto.getUrl(), ConvertUtil.paymentDtoToPayment(maybePayment.get(), decryptionService));
            }
            notificationRepository.delete(notificationDto);
        } catch (Exception e) {
            LOGGER.error("Web hook invocation failed", e);

            if (notificationDto.getRetries() <= maxRetries) {
                notificationDto.setProcessTimestamp(Instant.now());
                notificationDto.setRetries(notificationDto.getRetries() + 1);

                notificationRepository.save(notificationDto);
                taskScheduler.schedule(() -> processNotification(notificationDto), Instant.now().plusSeconds(retrySeconds));
            } else {
                LOGGER.error("Maximum number of retries ({}) exceeded, giving up", maxRetries);
                notificationRepository.delete(notificationDto);
            }
        }
    }
}
