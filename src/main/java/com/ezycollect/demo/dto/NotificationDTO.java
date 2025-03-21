package com.ezycollect.demo.dto;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.Instant;

@Entity(name = "Notification")
public class NotificationDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    private Integer paymentId;
    private String url;
    private Integer retries;
    private Instant createTimestamp;
    private Instant processTimestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Instant getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Instant createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public Instant getProcessTimestamp() {
        return processTimestamp;
    }

    public void setProcessTimestamp(Instant processTimestamp) {
        this.processTimestamp = processTimestamp;
    }

    @Override
    public String toString() {
        return "NotificationDTO{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", retries=" + retries +
                ", createTimestamp=" + createTimestamp +
                ", processTimestamp=" + processTimestamp +
                '}';
    }
}
