package com.maxime.smul_yas.mapper.crm;

import com.maxime.smul_yas.dto.crm_dto.CreditBalanceResponseDto;
import com.maxime.smul_yas.entity.Crm;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CrmMapper {
    CreditBalanceResponseDto toCreditBalanceResponseDto(Crm crm);
}
