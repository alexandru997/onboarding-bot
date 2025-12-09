package com.example.onboardingbot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class GroqService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public GroqService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public String chat(String userMessage, String context) {
        try {
            String systemPrompt = "You are a helpful onboarding assistant. Answer questions based on the provided documentation context. If the context doesn't contain relevant information, say so politely.";

            String fullPrompt = context.isEmpty()
                    ? userMessage
                    : "Context:\n" + context + "\n\nQuestion: " + userMessage;

            String jsonBody = objectMapper.writeValueAsString(new ChatRequest(
                    "llama-3.1-8b-instant",
                    new Message[]{
                            new Message("system", systemPrompt),
                            new Message("user", fullPrompt)
                    }
            ));

            Request request = new Request.Builder()
                    .url("https://api.groq.com/openai/v1/chat/completions")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response: " + response);
                }

                JsonNode jsonResponse = objectMapper.readTree(response.body().string());
                return jsonResponse.get("choices").get(0).get("message").get("content").asText();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to call Groq API", e);
        }
    }

    private record ChatRequest(String model, Message[] messages) {}
    private record Message(String role, String content) {}
}