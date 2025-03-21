package com.ezycollect.demo.dto;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "WebHook")
public class WebHookDTO {
    @Id
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "WebHookDTO{" +
                "url='" + url + '\'' +
                '}';
    }
}
