package com.example.online_examination_system.service;

import com.example.online_examination_system.dto.SubjectRequest;
import com.example.online_examination_system.dto.SubjectResponse;
import com.example.online_examination_system.entity.Subject;
import com.example.online_examination_system.repository.QuestionRepository;
import com.example.online_examination_system.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final QuestionRepository questionRepository;

    @Transactional(readOnly = true)
    public List<SubjectResponse> getAll() {
        return subjectRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SubjectResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public SubjectResponse create(SubjectRequest req) {
        String name = req.name().trim();

        if (existsIgnoreCase(name, null)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Is naam ka subject pehle se hai");
        }

        Subject subject = Subject.builder()
                .name(name)
                .description(clean(req.description()))
                .build();

        return toResponse(subjectRepository.save(subject));
    }

    @Transactional
    public SubjectResponse update(Long id, SubjectRequest req) {
        Subject subject = findOrThrow(id);
        String name = req.name().trim();

        if (existsIgnoreCase(name, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Is naam ka subject pehle se hai");
        }

        subject.setName(name);
        subject.setDescription(clean(req.description()));
        return toResponse(subjectRepository.save(subject));
    }

    @Transactional
    public void delete(Long id) {
        Subject subject = findOrThrow(id);

        if (questionRepository.countBySubject_Id(id) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Is subject mein questions hain, pehle unhe hataiye ya doosre subject mein daaliye");
        }
        subjectRepository.delete(subject);
    }

    // ================= helper methods =================

    private Subject findOrThrow(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject nahi mila"));
    }

    // excludeId: update ke waqt khud ko chhod kar baaki mein naam dhundhna
    private boolean existsIgnoreCase(String name, Long excludeId) {
        return subjectRepository.findAll().stream()
                .anyMatch(s -> s.getName().equalsIgnoreCase(name)
                        && (excludeId == null || !s.getId().equals(excludeId)));
    }

    private String clean(String text) {
        return (text == null || text.isBlank()) ? null : text.trim();
    }

    private SubjectResponse toResponse(Subject s) {
        return new SubjectResponse(
                s.getId(),
                s.getName(),
                s.getDescription(),
                questionRepository.countBySubject_Id(s.getId()));
    }
}