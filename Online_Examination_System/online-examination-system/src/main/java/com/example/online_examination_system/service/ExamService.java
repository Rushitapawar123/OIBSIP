package com.example.online_examination_system.service;

import com.example.online_examination_system.dto.ExamQuestionSummary;
import com.example.online_examination_system.dto.ExamRequest;
import com.example.online_examination_system.dto.ExamResponse;
import com.example.online_examination_system.entity.*;
import com.example.online_examination_system.repository.ExamRepository;
import com.example.online_examination_system.repository.QuestionRepository;
import com.example.online_examination_system.repository.SubjectRepository;
import com.example.online_examination_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    // ---------- LIST ----------
    @Transactional(readOnly = true)
    public List<ExamResponse> list(String email) {
        User user = findUser(email);
        List<Exam> exams = user.getRole() == Role.ADMIN
                ? examRepository.findAll().stream()
                    .sorted(Comparator.comparing(Exam::getId).reversed()).toList()
                : examRepository.findByCreatedBy_IdOrderByCreatedAtDesc(user.getId());
        return exams.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ExamResponse getById(Long id, String email) {
        User user = findUser(email);
        Exam exam = findOrThrow(id);
        checkOwnership(exam, user);
        return toResponse(exam);
    }

    // ---------- CREATE ----------
    @Transactional
    public ExamResponse create(ExamRequest req, String email) {
        User creator = findUser(email);
        validateTimes(req);

        Exam exam = Exam.builder()
                .createdBy(creator)
                .status(ExamStatus.DRAFT)
                .build();
        applyRequest(exam, req);

        return toResponse(examRepository.save(exam));
    }

    // ---------- UPDATE ----------
    @Transactional
    public ExamResponse update(Long id, ExamRequest req, String email) {
        User user = findUser(email);
        Exam exam = findOrThrow(id);
        checkOwnership(exam, user);
        requireDraft(exam, "Only draft exams can be edited");
        validateTimes(req);

        applyRequest(exam, req);
        return toResponse(examRepository.save(exam));
    }

    // ---------- DELETE ----------
    @Transactional
    public void delete(Long id, String email) {
        User user = findUser(email);
        Exam exam = findOrThrow(id);
        checkOwnership(exam, user);
        requireDraft(exam, "Only draft exams can be deleted");
        examRepository.delete(exam);
    }

    // ---------- PUBLISH ----------
    @Transactional
    public ExamResponse publish(Long id, String email) {
        User user = findUser(email);
        Exam exam = findOrThrow(id);
        checkOwnership(exam, user);
        requireDraft(exam, "Only draft exams can be published");

        if (exam.getQuestions().isEmpty()) {
            throw bad("Add at least one question before publishing");
        }
        if (exam.getPassMarks().compareTo(exam.getTotalMarks()) > 0) {
            throw bad("Pass marks cannot be more than total marks");
        }
        if (!exam.getEndTime().isAfter(LocalDateTime.now())) {
            throw bad("The end time has already passed. Update the schedule first");
        }

        exam.setStatus(ExamStatus.PUBLISHED);
        return toResponse(examRepository.save(exam));
    }

    // ---------- CLOSE ----------
    @Transactional
    public ExamResponse close(Long id, String email) {
        User user = findUser(email);
        Exam exam = findOrThrow(id);
        checkOwnership(exam, user);

        if (exam.getStatus() != ExamStatus.PUBLISHED) {
            throw bad("Only published exams can be closed");
        }
        exam.setStatus(ExamStatus.CLOSED);
        return toResponse(examRepository.save(exam));
    }

    // ================= helper methods =================

    private void applyRequest(Exam exam, ExamRequest req) {
        Subject subject = subjectRepository.findById(req.subjectId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found"));

        Set<Question> questions = loadQuestions(req.questionIds(), subject.getId());
        BigDecimal totalMarks = questions.stream()
                .map(Question::getMarks)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (!questions.isEmpty() && req.passMarks().compareTo(totalMarks) > 0) {
            throw bad("Pass marks (" + req.passMarks() + ") cannot be more than total marks (" + totalMarks + ")");
        }

        exam.setTitle(req.title().trim());
        exam.setDescription(clean(req.description()));
        exam.setSubject(subject);
        exam.setDurationMinutes(req.durationMinutes());
        exam.setPassMarks(req.passMarks());
        exam.setNegativeMarking(req.negativeMarking());
        exam.setStartTime(req.startTime());
        exam.setEndTime(req.endTime());
        exam.setShuffleQuestions(req.shuffleQuestions());
        exam.setShuffleOptions(req.shuffleOptions());
        exam.setAdaptive(req.adaptive());
        exam.setProctoringEnabled(req.proctoringEnabled());
        exam.setWebcamRequired(req.webcamRequired());
        exam.setCertificateEnabled(req.certificateEnabled());
        exam.setMaxViolations(req.maxViolations());
        exam.setTotalMarks(totalMarks);

        exam.getQuestions().clear();
        exam.getQuestions().addAll(questions);
    }

    private Set<Question> loadQuestions(List<Long> ids, Long subjectId) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        Set<Long> uniqueIds = new HashSet<>(ids);
        List<Question> found = questionRepository.findAllById(uniqueIds);

        if (found.size() != uniqueIds.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Some selected questions were not found");
        }
        for (Question q : found) {
            if (!q.getSubject().getId().equals(subjectId)) {
                throw bad("All questions must belong to the exam's subject");
            }
            if (!Boolean.TRUE.equals(q.getApproved())) {
                throw bad("Question #" + q.getId() + " is not approved yet");
            }
        }
        return new HashSet<>(found);
    }

    private void validateTimes(ExamRequest req) {
        if (!req.endTime().isAfter(req.startTime())) {
            throw bad("End time must be after the start time");
        }
        if (!req.startTime().isAfter(LocalDateTime.now())) {
            throw bad("Start time must be in the future");
        }
        long windowMinutes = java.time.Duration.between(req.startTime(), req.endTime()).toMinutes();
        if (windowMinutes < req.durationMinutes()) {
            throw bad("The exam window (" + windowMinutes + " min) is shorter than the duration ("
                    + req.durationMinutes() + " min)");
        }
    }

    private void requireDraft(Exam exam, String message) {
        if (exam.getStatus() != ExamStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, message);
        }
    }

    private void checkOwnership(Exam exam, User user) {
        if (user.getRole() == Role.ADMIN) {
            return;
        }
        if (!exam.getCreatedBy().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only manage your own exams");
        }
    }

    private Exam findOrThrow(Long id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found"));
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private String clean(String text) {
        return (text == null || text.isBlank()) ? null : text.trim();
    }

    private ExamResponse toResponse(Exam e) {
        List<ExamQuestionSummary> questions = e.getQuestions().stream()
                .sorted(Comparator.comparing(Question::getId))
                .map(q -> new ExamQuestionSummary(
                        q.getId(), q.getQuestionText(), q.getDifficulty(), q.getMarks(), q.getTopic()))
                .toList();

        return new ExamResponse(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getSubject().getId(),
                e.getSubject().getName(),
                e.getDurationMinutes(),
                e.getTotalMarks(),
                e.getPassMarks(),
                e.getNegativeMarking(),
                e.getStartTime(),
                e.getEndTime(),
                Boolean.TRUE.equals(e.getShuffleQuestions()),
                Boolean.TRUE.equals(e.getShuffleOptions()),
                Boolean.TRUE.equals(e.getAdaptive()),
                Boolean.TRUE.equals(e.getProctoringEnabled()),
                Boolean.TRUE.equals(e.getWebcamRequired()),
                Boolean.TRUE.equals(e.getCertificateEnabled()),
                e.getMaxViolations(),
                e.getStatus(),
                questions.size(),
                e.getCreatedBy().getFullName(),
                questions);
    }
}