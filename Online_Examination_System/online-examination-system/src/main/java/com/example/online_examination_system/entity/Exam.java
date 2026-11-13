package com.example.online_examination_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "exams")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "total_marks", nullable = false, precision = 6, scale = 2)
    @Builder.Default
    private BigDecimal totalMarks = BigDecimal.ZERO;

    @Column(name = "pass_marks", nullable = false, precision = 6, scale = 2)
    @Builder.Default
    private BigDecimal passMarks = BigDecimal.ZERO;

    @Column(name = "negative_marking", nullable = false, precision = 4, scale = 2)
    @Builder.Default
    private BigDecimal negativeMarking = BigDecimal.ZERO;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "shuffle_questions", nullable = false)
    @Builder.Default
    private Boolean shuffleQuestions = true;

    @Column(name = "shuffle_options", nullable = false)
    @Builder.Default
    private Boolean shuffleOptions = true;

    @Column(name = "is_adaptive", nullable = false)
    @Builder.Default
    private Boolean adaptive = false;

    @Column(name = "proctoring_enabled", nullable = false)
    @Builder.Default
    private Boolean proctoringEnabled = true;

    @Column(name = "webcam_required", nullable = false)
    @Builder.Default
    private Boolean webcamRequired = false;

    @Column(name = "certificate_enabled", nullable = false)
    @Builder.Default
    private Boolean certificateEnabled = true;

    @Column(name = "max_violations", nullable = false)
    @Builder.Default
    private Integer maxViolations = 3;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ExamStatus status = ExamStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
        name = "exam_questions",
        joinColumns = @JoinColumn(name = "exam_id"),
        inverseJoinColumns = @JoinColumn(name = "question_id"))
    @Builder.Default
    private Set<Question> questions = new HashSet<>();
}