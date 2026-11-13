
package com.example.online_examination_system.repository;

import com.example.online_examination_system.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findByAttempt_Id(Long attemptId);

    Optional<Answer> findByAttempt_IdAndQuestion_Id(Long attemptId, Long questionId);

    // Student ka topic-wise report: [topic, total jawab, sahi jawab]
    @Query("""
           select q.topic, count(a), sum(case when a.correct = true then 1 else 0 end)
           from Answer a join a.question q
           where a.attempt.student.id = :studentId and q.topic is not null
           group by q.topic
           """)
    List<Object[]> topicPerformanceByStudent(@Param("studentId") Long studentId);

    // Question difficulty analysis: [question id, total jawab, sahi jawab]
    @Query("""
           select a.question.id, count(a), sum(case when a.correct = true then 1 else 0 end)
           from Answer a
           where a.attempt.exam.id = :examId
           group by a.question.id
           """)
    List<Object[]> questionStatsByExam(@Param("examId") Long examId);
}