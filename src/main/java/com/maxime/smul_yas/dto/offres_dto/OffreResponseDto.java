package com.maxime.smul_yas.dto.offres_dto;

import com.maxime.smul_yas.enums.CategoriesType;
import com.maxime.smul_yas.enums.OffresType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OffreResponseDto {
    //private String forfaitsId;
    private CategoriesType categories;
    private OffresType type;
    private String appelation;
    private BigDecimal price;
    private BigDecimal voice;
    private long sms;
    private long internet;
    private int validite;
    private boolean status;
}
