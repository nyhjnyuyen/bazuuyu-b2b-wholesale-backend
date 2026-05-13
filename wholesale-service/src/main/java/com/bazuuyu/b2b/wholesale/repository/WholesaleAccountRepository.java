package com.bazuuyu.b2b.wholesale.repository;

import com.bazuuyu.b2b.wholesale.entity.WholesaleAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WholesaleAccountRepository extends JpaRepository<WholesaleAccount, Long> {
    Optional<WholesaleAccount> findByUsername(String username);
    Optional<WholesaleAccount> findByUserId(Long userId);
    Optional<WholesaleAccount> findByEmail(String email);
    boolean existsByUsername(String username);
}
