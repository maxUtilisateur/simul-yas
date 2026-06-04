package com.maxime.smul_yas.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_forfaits")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserForfait {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crm_id", nullable = false)
    private Crm crm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offres_id", nullable = false)
    private Offres offres;

    @Column(precision = 15, scale = 2)
    private BigDecimal voiceRemaining;

    @Column(nullable = false)
    private long smsRemaining;

    @Column(nullable = false)
    private long internetRemaining;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @Column(nullable = false)
    private boolean active;
}
