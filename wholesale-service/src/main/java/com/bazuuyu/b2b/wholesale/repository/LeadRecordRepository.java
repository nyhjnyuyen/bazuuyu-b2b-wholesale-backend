package com.bazuuyu.b2b.wholesale.repository;

import com.bazuuyu.b2b.wholesale.entity.LeadRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeadRecordRepository extends JpaRepository<LeadRecord, Long> {
    Optional<LeadRecord> findByEmail(String email);
}
