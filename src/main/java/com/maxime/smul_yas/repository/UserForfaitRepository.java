package com.maxime.smul_yas.repository;

import com.maxime.smul_yas.entity.Crm;
import com.maxime.smul_yas.entity.UserForfait;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserForfaitRepository extends JpaRepository<UserForfait, Long> {
    List<UserForfait> findByCrmPhoneAndActiveTrue(String phone);
    List<UserForfait> findByCrmAndActiveTrueAndExpirationDateBefore(Crm crm, LocalDateTime date);
}
