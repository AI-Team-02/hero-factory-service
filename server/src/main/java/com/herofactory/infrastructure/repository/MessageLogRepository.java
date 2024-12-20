package com.herofactory.infrastructure.repository;

import com.herofactory.infrastructure.entity.MessageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageLogRepository extends JpaRepository<MessageLog, Long> {
    // 기존 payment 서비스 관련 메서드
    boolean existsByMessageIdAndStatus(String messageId, String status);

    Optional<MessageLog> findTopByMessageIdOrderByCreatedAtDesc(String messageId);

    @Query("SELECT ml FROM MessageLog ml WHERE ml.paymentId = :paymentId ORDER BY ml.createdAt DESC")
    List<MessageLog> findMessageLogsByPaymentId(@Param("paymentId") String paymentId);

    @Query("SELECT ml FROM MessageLog ml WHERE ml.status = :status AND ml.createdAt < :cutoffTime")
    List<MessageLog> findStaleMessages(@Param("status") String status,
                                       @Param("cutoffTime") LocalDateTime cutoffTime);

    @Query("SELECT COUNT(ml) FROM MessageLog ml WHERE ml.messageId = :messageId AND ml.status = 'FAILED'")
    int countFailedAttempts(@Param("messageId") String messageId);

    // Prompt 서비스를 위한 새로운 메서드
    @Query("SELECT ml FROM MessageLog ml WHERE ml.promptId = :promptId ORDER BY ml.createdAt DESC")
    List<MessageLog> findMessageLogsByPromptId(@Param("promptId") String promptId);

    @Query("SELECT ml FROM MessageLog ml WHERE ml.messageId = :messageId AND ml.status = :status AND ml.promptId = :promptId")
    Optional<MessageLog> findByMessageIdAndStatusAndPromptId(
            @Param("messageId") String messageId,
            @Param("status") String status,
            @Param("promptId") String promptId
    );

    @Query("SELECT COUNT(ml) FROM MessageLog ml WHERE ml.promptId = :promptId AND ml.status = :status")
    int countByPromptIdAndStatus(
            @Param("promptId") String promptId,
            @Param("status") String status
    );

    @Query("SELECT ml FROM MessageLog ml WHERE ml.status = :status AND ml.promptId IS NOT NULL AND ml.createdAt < :cutoffTime")
    List<MessageLog> findStalePromptMessages(
            @Param("status") String status,
            @Param("cutoffTime") LocalDateTime cutoffTime
    );

    @Query("SELECT DISTINCT ml.promptId FROM MessageLog ml WHERE ml.status = :status")
    List<String> findDistinctPromptIdsByStatus(@Param("status") String status);
}