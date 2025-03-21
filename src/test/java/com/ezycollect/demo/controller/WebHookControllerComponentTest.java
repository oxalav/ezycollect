package com.ezycollect.demo.controller;

import com.ezycollect.demo.repository.WebHookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext
class WebHookControllerComponentTest {
    private static final String API_WEBHOOK_REGISTER_PATH = "/api/v1/webhook/register";
    private static final String API_WEBHOOK_UNREGISTER_PATH = "/api/v1/webhook/unregister";
    private static final String WEBHOOK_URL = "http://localhost:12345/callback";
    private static final String UNKNOWN_URL = "http://localhost:12345/unknown";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebHookRepository webHookRepository;

    @Test
    @DirtiesContext
    public void shouldRegisterWebHook() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(API_WEBHOOK_REGISTER_PATH).param("url", WEBHOOK_URL))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Registered"));

        assertThat(webHookRepository.findAll()).anyMatch(dto -> Objects.equals(WEBHOOK_URL, dto.getUrl()));
    }

    @Test
    @DirtiesContext
    public void shouldUnregisterWebHook() throws Exception {
        shouldRegisterWebHook();

        mockMvc.perform(MockMvcRequestBuilders.post(API_WEBHOOK_UNREGISTER_PATH).param("url", WEBHOOK_URL))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Unregistered"));

        assertThat(webHookRepository.findAll()).isEmpty();
    }

    @Test
    public void shouldFailToUnregisterUnknownWebHook() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(API_WEBHOOK_UNREGISTER_PATH).param("url", UNKNOWN_URL))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
