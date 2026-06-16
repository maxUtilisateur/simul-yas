package com.maxime.smul_yas.dto.offres_dto;

import com.maxime.smul_yas.enums.CategoriesType;
import com.maxime.smul_yas.enums.OffresType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ActiveForfaitResponseDto {
    //private String forfaitId;
    private String appelation;
    private CategoriesType category;
    private OffresType type;
    private BigDecimal voiceRemaining;
    private long smsRemaining;
    private long internetRemaining;
    private LocalDateTime expirationDate;
    private boolean active;
}
