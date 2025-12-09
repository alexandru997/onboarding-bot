package com.example.onboardingbot.service;

import com.example.onboardingbot.entity.Document;
import com.example.onboardingbot.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final EmbeddingService embeddingService;

    public DocumentService(DocumentRepository documentRepository, EmbeddingService embeddingService) {
        this.documentRepository = documentRepository;
        this.embeddingService = embeddingService;
    }

    @Transactional
    public Document saveDocument(String title, String content) {
        float[] embeddingArray = embeddingService.generateEmbedding(content);
        String embedding = embeddingService.embeddingToString(embeddingArray);
        documentRepository.insertDocument(title, content, embedding);
        return documentRepository.findLastInserted();
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Optional<Document> getDocumentById(Long id) {
        return documentRepository.findById(id);
    }

    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
    }

    public List<Document> findSimilarDocuments(String query, int limit) {
        float[] queryEmbedding = embeddingService.generateEmbedding(query);
        String embeddingString = embeddingService.embeddingToString(queryEmbedding);
        return documentRepository.findSimilarDocuments(embeddingString, limit);
    }
}