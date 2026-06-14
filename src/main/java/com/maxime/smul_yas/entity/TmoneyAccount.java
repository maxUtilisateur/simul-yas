package com.maxime.smul_yas.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tmoney_accounts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TmoneyAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String phone;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private String password;
}
