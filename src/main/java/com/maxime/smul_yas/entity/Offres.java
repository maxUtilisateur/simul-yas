package com.maxime.smul_yas.entity;

import com.maxime.smul_yas.enums.CategoriesType;
import com.maxime.smul_yas.enums.OffresType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "offres_forfaits")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Offres {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column()
    private String forfaits_id;

    @Column()
    @Enumerated(EnumType.STRING)
    private CategoriesType categories;

    @Column()
    @Enumerated(EnumType.STRING)
    private OffresType type;

    @Column()
    private String appelation;

    @Column(precision = 15, scale = 0)
    private BigDecimal price;

    @Column(precision = 15, scale = 0)
    private BigDecimal voice;

    @Column()
    private long sms;

    @Column
    private long internet;

    @Column()
    private int validite;

    @Column()
    private boolean status;

}
