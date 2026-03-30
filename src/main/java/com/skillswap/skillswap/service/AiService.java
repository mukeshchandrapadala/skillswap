package com.skillswap.skillswap.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

@Service
public class AiService {

    @Value("${openai.api.key}")
    private String apiKey;

    public String askAI(String message) {

        RestTemplate restTemplate = new RestTemplate();

        String url = "https://openrouter.ai/api/v1/chat/completions";

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4o-mini");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", message));

        body.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        List choices = (List) response.getBody().get("choices");
        Map choice = (Map) choices.get(0);
        Map msg = (Map) choice.get("message");

        return msg.get("content").toString();
    }
    
    // 🔹 AI Reply Suggestion
    public String suggestReply(String message) {
        return askAI("Suggest a short reply to this message: " + message);
    }
    
    // 🔹 AI Skill Match
    public String matchSkills(String skillA, String skillB) {
        return askAI("Compare these skills and give match score (0-100) with reason:\n"
            + "User A: " + skillA + "\nUser B: " + skillB);
        }

}