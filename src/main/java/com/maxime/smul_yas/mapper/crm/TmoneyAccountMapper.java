package com.maxime.smul_yas.mapper.crm;

import com.maxime.smul_yas.dto.crm_dto.TmoneyAccountResponseDto;
import com.maxime.smul_yas.entity.TmoneyAccount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TmoneyAccountMapper {
    TmoneyAccountResponseDto toTmoneyAccountResponseDto(TmoneyAccount account);
}
