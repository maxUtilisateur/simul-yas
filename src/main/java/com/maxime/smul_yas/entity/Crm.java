package com.maxime.smul_yas.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "bd_crm")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Crm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column()
    private String user_id ;

    @Column()
    private String firstName ;

    @Column()
    private String lastName ;

    @Column()
    private String email ;

    @Column()
    private String city ;

    @Column()
    private String phone ;

    @Column(precision = 15, scale = 2)
    private BigDecimal creditBalance;
}
