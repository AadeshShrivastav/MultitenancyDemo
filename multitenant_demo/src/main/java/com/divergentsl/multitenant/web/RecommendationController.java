package com.divergentsl.multitenant.web;

import com.divergentsl.multitenant.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/recommendation")
@AllArgsConstructor
public class RecommendationController {
    private final MessageService service;

    @GetMapping
    public Map<String, String> recommend(@RequestParam Long threadId) {
        return Map.of("action", service.getRecommendation(threadId));
    }
}