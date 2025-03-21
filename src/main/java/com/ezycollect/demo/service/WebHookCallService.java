package com.ezycollect.demo.service;

import com.ezycollect.demo.model.Payment;

public interface WebHookCallService {
    void callWebHook(String url, Payment payment);
}
