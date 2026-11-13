package com.example.online_examination_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "answers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attempt_id")
    private Attempt attempt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "selected_option_ids", length = 100)
    private String selectedOptionIds;

    @Column(name = "marked_for_review", nullable = false)
    @Builder.Default
    private Boolean markedForReview = false;

    @Column(name = "is_correct")
    private Boolean correct;

    @Column(name = "marks_obtained", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal marksObtained = BigDecimal.ZERO;

    @Column(name = "time_spent_seconds", nullable = false)
    @Builder.Default
    private Integer timeSpentSeconds = 0;

    @Column(name = "saved_at", insertable = false, updatable = false)
    private LocalDateTime savedAt;
}