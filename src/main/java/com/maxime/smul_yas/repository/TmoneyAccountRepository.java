package com.maxime.smul_yas.repository;

import com.maxime.smul_yas.entity.TmoneyAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TmoneyAccountRepository extends JpaRepository<TmoneyAccount, Long> {
    Optional<TmoneyAccount> findByPhone(String phone);
}
