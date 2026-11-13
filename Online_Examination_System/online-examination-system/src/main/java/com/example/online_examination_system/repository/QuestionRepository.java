package com.example.online_examination_system.repository;

import com.example.online_examination_system.entity.Difficulty;
import com.example.online_examination_system.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    Page<Question> findBySubject_Id(Long subjectId, Pageable pageable);

    Page<Question> findBySubject_IdAndDifficulty(Long subjectId, Difficulty difficulty, Pageable pageable);

    List<Question> findByApprovedFalse();

    List<Question> findBySubject_IdAndDifficultyAndApprovedTrue(Long subjectId, Difficulty difficulty);

    long countBySubject_Id(Long subjectId);

    // Exam ke saare questions (exam engine aur adaptive testing ke liye)
    @Query("select q from Exam e join e.questions q where e.id = :examId")
    List<Question> findAllByExamId(@Param("examId") Long examId);

    @Query("select q from Exam e join e.questions q where e.id = :examId and q.difficulty = :difficulty")
    List<Question> findByExamIdAndDifficulty(@Param("examId") Long examId,
                                             @Param("difficulty") Difficulty difficulty);
}
