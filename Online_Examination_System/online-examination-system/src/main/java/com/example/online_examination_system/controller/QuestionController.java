package com.example.online_examination_system.controller;

import com.example.online_examination_system.dto.QuestionRequest;
import com.example.online_examination_system.dto.QuestionResponse;
import com.example.online_examination_system.entity.Difficulty;
import com.example.online_examination_system.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/teacher/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    public Page<QuestionResponse> list(
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return questionService.list(subjectId, difficulty, page, size);
    }

    @GetMapping("/{id}")
    public QuestionResponse getById(@PathVariable Long id) {
        return questionService.getById(id);
    }

    @PostMapping
    public ResponseEntity<QuestionResponse> create(@Valid @RequestBody QuestionRequest req,
                                                   Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(questionService.create(req, auth.getName()));
    }

    @PutMapping("/{id}")
    public QuestionResponse update(@PathVariable Long id,
                                   @Valid @RequestBody QuestionRequest req,
                                   Authentication auth) {
        return questionService.update(id, req, auth.getName());
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id, Authentication auth) {
        questionService.delete(id, auth.getName());
        return Map.of("message", "Question delete ho gaya");
    }
}