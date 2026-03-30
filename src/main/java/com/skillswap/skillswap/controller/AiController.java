package com.skillswap.skillswap.controller;

import com.skillswap.skillswap.service.AiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/test")
    public String testAI() {
        return "AI is working";
    }

    @GetMapping("/ask")
    public String ask(@RequestParam String message) {
        return aiService.askAI(message);
    }

    @GetMapping("/suggest")
    public String suggest(@RequestParam String message) {
        return aiService.suggestReply(message);
    }
    
    @GetMapping("/match")
    public String match(@RequestParam String a, @RequestParam String b) {
        return aiService.matchSkills(a, b);
    }
}