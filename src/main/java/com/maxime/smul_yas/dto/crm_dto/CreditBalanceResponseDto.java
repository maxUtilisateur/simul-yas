package com.maxime.smul_yas.dto.crm_dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreditBalanceResponseDto {
    private String phone;
    private BigDecimal creditBalance;
}
