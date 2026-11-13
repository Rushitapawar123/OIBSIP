package com.example.online_examination_system.service;

import com.example.online_examination_system.dto.OptionRequest;
import com.example.online_examination_system.dto.OptionResponse;
import com.example.online_examination_system.dto.QuestionRequest;
import com.example.online_examination_system.dto.QuestionResponse;
import com.example.online_examination_system.entity.*;
import com.example.online_examination_system.repository.QuestionRepository;
import com.example.online_examination_system.repository.SubjectRepository;
import com.example.online_examination_system.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // ---------- LIST (pagination + filter) ----------
    @Transactional(readOnly = true)
    public Page<QuestionResponse> list(Long subjectId, Difficulty difficulty, int page, int size) {
        int safeSize = Math.min(Math.max(size, 1), 50);
        Pageable pageable = PageRequest.of(Math.max(page, 0), safeSize, Sort.by("id").descending());

        Page<Question> result;
        if (subjectId == null && difficulty != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Difficulty se filter karne ke liye subjectId bhi dijiye");
        } else if (subjectId == null) {
            result = questionRepository.findAll(pageable);
        } else if (difficulty == null) {
            result = questionRepository.findBySubject_Id(subjectId, pageable);
        } else {
            result = questionRepository.findBySubject_IdAndDifficulty(subjectId, difficulty, pageable);
        }
        return result.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public QuestionResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    // ---------- CREATE ----------
    @Transactional
    public QuestionResponse create(QuestionRequest req, String email) {
        User creator = findUser(email);
        validateOptions(req);

        Question question = Question.builder()
                .subject(findSubject(req.subjectId()))
                .createdBy(creator)
                .build();
        applyRequest(question, req);

        return toResponse(questionRepository.save(question));
    }

    // ---------- UPDATE ----------
    @Transactional
    public QuestionResponse update(Long id, QuestionRequest req, String email) {
        User user = findUser(email);
        Question question = findOrThrow(id);
        checkOwnership(question, user);
        validateOptions(req);

        question.setSubject(findSubject(req.subjectId()));
        applyRequest(question, req);

        return toResponse(questionRepository.save(question));
    }

    // ---------- DELETE ----------
    @Transactional
    public void delete(Long id, String email) {
        User user = findUser(email);
        Question question = findOrThrow(id);
        checkOwnership(question, user);

        Number usedInExams = (Number) entityManager
                .createNativeQuery("select count(*) from exam_questions where question_id = :qid")
                .setParameter("qid", id)
                .getSingleResult();

        if (usedInExams.longValue() > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ye question kisi exam mein juda hai, pehle use exam se hataiye");
        }
        questionRepository.delete(question);
    }

    // ================= helper methods =================

    // Request ka data question mein bharta hai (create aur update dono ke liye)
    private void applyRequest(Question q, QuestionRequest req) {
        q.setQuestionText(req.questionText().trim());
        q.setQuestionType(req.questionType());
        q.setDifficulty(req.difficulty());
        q.setMarks(req.marks());
        q.setTopic(clean(req.topic()));
        q.setExplanation(clean(req.explanation()));

        // Purane options hatakar naye daalna (orphanRemoval purane rows delete kar deta hai)
        q.getOptions().clear();
        for (OptionRequest o : req.options()) {
            q.getOptions().add(QuestionOption.builder()
                    .question(q)
                    .optionText(o.optionText().trim())
                    .correct(o.correct())
                    .build());
        }
    }

    private void validateOptions(QuestionRequest req) {
        long correctCount = req.options().stream().filter(OptionRequest::correct).count();

        switch (req.questionType()) {
            case SINGLE -> {
                if (correctCount != 1) {
                    throw bad("SINGLE question mein exactly 1 sahi option hona chahiye");
                }
            }
            case MULTIPLE -> {
                if (correctCount < 1) {
                    throw bad("MULTIPLE question mein kam se kam 1 sahi option hona chahiye");
                }
            }
            case TRUE_FALSE -> {
                if (req.options().size() != 2 || correctCount != 1) {
                    throw bad("TRUE_FALSE question mein exactly 2 options aur 1 sahi jawab hona chahiye");
                }
            }
        }
    }

    // Teacher sirf apna question chhu sakta hai, Admin sab
    private void checkOwnership(Question question, User user) {
        if (user.getRole() == Role.ADMIN) {
            return;
        }
        if (!question.getCreatedBy().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Aap sirf apne banaye hue questions badal ya delete kar sakte hain");
        }
    }

    private Question findOrThrow(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question nahi mila"));
    }

    private Subject findSubject(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject nahi mila"));
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User nahi mila"));
    }

    private ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private String clean(String text) {
        return (text == null || text.isBlank()) ? null : text.trim();
    }

    private QuestionResponse toResponse(Question q) {
        List<OptionResponse> options = q.getOptions().stream()
                .map(o -> new OptionResponse(o.getId(), o.getOptionText(), Boolean.TRUE.equals(o.getCorrect())))
                .toList();

        return new QuestionResponse(
                q.getId(),
                q.getSubject().getId(),
                q.getSubject().getName(),
                q.getQuestionText(),
                q.getQuestionType(),
                q.getDifficulty(),
                q.getMarks(),
                q.getTopic(),
                q.getExplanation(),
                Boolean.TRUE.equals(q.getAiGenerated()),
                Boolean.TRUE.equals(q.getApproved()),
                options);
    }
}