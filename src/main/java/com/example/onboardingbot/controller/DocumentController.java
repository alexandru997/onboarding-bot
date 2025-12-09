package com.example.onboardingbot.controller;

import com.example.onboardingbot.entity.Document;
import com.example.onboardingbot.service.DocumentService;
import com.example.onboardingbot.service.GroqService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class DocumentController {

    private final DocumentService documentService;
    private final GroqService groqService;

    public DocumentController(DocumentService documentService, GroqService groqService) {
        this.documentService = documentService;
        this.groqService = groqService;
    }


    @PostMapping("/documents")
    public ResponseEntity<Document> addDocument(@RequestBody DocumentRequest request) {
        Document saved = documentService.saveDocument(request.title(), request.content());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/documents")
    public ResponseEntity<List<Document>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @GetMapping("/documents/{id}")
    public ResponseEntity<Document> getDocument(@PathVariable Long id) {
        return documentService.getDocumentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/documents/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        List<Document> similarDocs = documentService.findSimilarDocuments(request.question(), 3);

        String context = similarDocs.stream()
                .map(doc -> "Title: " + doc.getTitle() + "\n" + doc.getContent())
                .collect(Collectors.joining("\n\n---\n\n"));

        String answer = groqService.chat(request.question(), context);

        return ResponseEntity.ok(new ChatResponse(answer, similarDocs.size()));
    }

    record DocumentRequest(String title, String content) {
    }

    record ChatRequest(String question) {
    }

    record ChatResponse(String answer, int sourcesUsed) {
    }
}