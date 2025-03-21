package com.ezycollect.demo.controller;

import com.ezycollect.demo.dto.WebHookDTO;
import com.ezycollect.demo.repository.WebHookRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Controller
@RequestMapping(path = "/api/v1/webhook")
public class WebHookController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebHookController.class);

    @Autowired
    private WebHookRepository webHookRepository;

    @PostMapping(path = "/register")
    @Transactional
    public @ResponseBody ResponseEntity<String> register(@RequestParam("url") String url) {
        LOGGER.info("Registering new Web hook: {}", url);

        WebHookDTO webHookDTO = new WebHookDTO();
        webHookDTO.setUrl(url);

        webHookRepository.save(webHookDTO);

        LOGGER.info("Web hook successfully registered");
        return ResponseEntity.ok().body("Registered");
    }

    @PostMapping(path = "/unregister")
    @Transactional
    public @ResponseBody ResponseEntity<String> unregister(@RequestParam("url") String url) {
        LOGGER.info("Unregistering new Web hook: {}", url);

        Optional<WebHookDTO> result = webHookRepository.findById(url);

        if (result.isPresent()) {
            webHookRepository.delete(result.get());

            LOGGER.info("Web hook successfully unregistered");
            return ResponseEntity.ok().body("Unregistered");
        }

        LOGGER.info("Web hook does not exist");
        return ResponseEntity.badRequest().body("Not found");
    }
}
