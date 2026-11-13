package com.example.online_examination_system.controller;

import com.example.online_examination_system.dto.SubjectRequest;
import com.example.online_examination_system.dto.SubjectResponse;
import com.example.online_examination_system.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    public List<SubjectResponse> getAll() {
        return subjectService.getAll();
    }

    @GetMapping("/{id}")
    public SubjectResponse getById(@PathVariable Long id) {
        return subjectService.getById(id);
    }

    @PostMapping
    public ResponseEntity<SubjectResponse> create(@Valid @RequestBody SubjectRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subjectService.create(req));
    }

    @PutMapping("/{id}")
    public SubjectResponse update(@PathVariable Long id, @Valid @RequestBody SubjectRequest req) {
        return subjectService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        subjectService.delete(id);
        return Map.of("message", "Subject delete ho gaya");
    }
}