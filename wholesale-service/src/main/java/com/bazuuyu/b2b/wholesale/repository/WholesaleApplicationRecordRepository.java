package com.bazuuyu.b2b.wholesale.repository;

import com.bazuuyu.b2b.wholesale.entity.WholesaleApplicationRecord;
import com.bazuuyu.b2b.wholesale.entity.enums.ApplicationReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WholesaleApplicationRecordRepository extends JpaRepository<WholesaleApplicationRecord, Long> {
    Optional<WholesaleApplicationRecord> findTopByUsernameOrderBySubmittedAtDesc(String username);
    Optional<WholesaleApplicationRecord> findTopByUserIdOrderBySubmittedAtDesc(Long userId);
    Optional<WholesaleApplicationRecord> findTopByEmailOrderBySubmittedAtDesc(String email);
    List<WholesaleApplicationRecord> findByReviewStatus(ApplicationReviewStatus reviewStatus);
    boolean existsByUserIdAndReviewStatusIn(Long userId, List<ApplicationReviewStatus> statuses);
    boolean existsByEmailAndReviewStatusIn(String email, List<ApplicationReviewStatus> statuses);
}
