package com.example.onboardingbot.service;

import org.springframework.stereotype.Service;

@Service
public class EmbeddingService {

    private static final int EMBEDDING_SIZE = 384;

    public float[] generateEmbedding(String text) {
        float[] embedding = new float[EMBEDDING_SIZE];
        String normalizedText = text.toLowerCase();
        String[] words = normalizedText.split("\\s+");

        for (String word : words) {
            int hash = word.hashCode();
            for (int i = 0; i < EMBEDDING_SIZE; i++) {
                embedding[i] += (float) Math.sin(hash * (i + 1)) * 0.1f;
            }
        }


        float norm = 0;
        for (float v : embedding) {
            norm += v * v;
        }
        norm = (float) Math.sqrt(norm);
        if (norm > 0) {
            for (int i = 0; i < embedding.length; i++) {
                embedding[i] /= norm;
            }
        }

        return embedding;
    }

    public String embeddingToString(float[] embedding) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            sb.append(embedding[i]);
            if (i < embedding.length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

}