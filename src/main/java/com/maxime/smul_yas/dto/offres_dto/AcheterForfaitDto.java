package com.maxime.smul_yas.dto.offres_dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AcheterForfaitDto {
    private String buyerPhone;
    private String beneficiaryPhone; // Optional, if empty/null, it defaults to buyerPhone
    private String forfaitsId;
}
