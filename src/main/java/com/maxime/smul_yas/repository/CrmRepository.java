package com.maxime.smul_yas.repository;

import com.maxime.smul_yas.entity.Crm;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CrmRepository extends JpaRepository<Crm, Long> {
    Optional<Crm> findByPhone(String phone);
}
