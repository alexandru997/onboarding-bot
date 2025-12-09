package com.example.onboardingbot.repository;

import com.example.onboardingbot.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByTitleContainingIgnoreCase(String title);

    @Query(value = "SELECT * FROM documents ORDER BY embedding <-> cast(:embedding as vector) LIMIT :limit", nativeQuery = true)
    List<Document> findSimilarDocuments(@Param("embedding") String embedding, @Param("limit") int limit);

    @Modifying
    @Query(value = "INSERT INTO documents (title, content, embedding, created_at) VALUES (:title, :content, cast(:embedding as vector), NOW())", nativeQuery = true)
    void insertDocument(@Param("title") String title, @Param("content") String content, @Param("embedding") String embedding);

    @Query(value = "SELECT * FROM documents WHERE id = (SELECT MAX(id) FROM documents)", nativeQuery = true)
    Document findLastInserted();
}