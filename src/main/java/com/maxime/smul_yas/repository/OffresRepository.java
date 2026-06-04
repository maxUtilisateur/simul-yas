package com.maxime.smul_yas.repository;

import com.maxime.smul_yas.entity.Offres;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface OffresRepository extends JpaRepository<Offres, Long> {

    @Query("SELECT o FROM Offres o WHERE o.forfaits_id = :forfaitsId")
    Optional<Offres> findByForfaitsId(@Param("forfaitsId") String forfaitsId);

    List<Offres> findByStatusTrue();
}
