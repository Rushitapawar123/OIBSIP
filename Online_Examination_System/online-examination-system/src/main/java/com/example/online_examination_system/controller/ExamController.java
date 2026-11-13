package com.example.online_examination_system.controller;

import com.example.online_examination_system.dto.ExamRequest;
import com.example.online_examination_system.dto.ExamResponse;
import com.example.online_examination_system.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @GetMapping
    public List<ExamResponse> list(Authentication auth) {
        return examService.list(auth.getName());
    }

    @GetMapping("/{id}")
    public ExamResponse getById(@PathVariable Long id, Authentication auth) {
        return examService.getById(id, auth.getName());
    }

    @PostMapping
    public ResponseEntity<ExamResponse> create(@Valid @RequestBody ExamRequest req,
                                               Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(examService.create(req, auth.getName()));
    }

    @PutMapping("/{id}")
    public ExamResponse update(@PathVariable Long id,
                               @Valid @RequestBody ExamRequest req,
                               Authentication auth) {
        return examService.update(id, req, auth.getName());
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id, Authentication auth) {
        examService.delete(id, auth.getName());
        return Map.of("message", "Exam deleted");
    }

    @PostMapping("/{id}/publish")
    public ExamResponse publish(@PathVariable Long id, Authentication auth) {
        return examService.publish(id, auth.getName());
    }

    @PostMapping("/{id}/close")
    public ExamResponse close(@PathVariable Long id, Authentication auth) {
        return examService.close(id, auth.getName());
    }
}