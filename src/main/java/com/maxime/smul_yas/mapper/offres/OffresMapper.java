package com.maxime.smul_yas.mapper.offres;

import com.maxime.smul_yas.dto.offres_dto.ActiveForfaitResponseDto;
import com.maxime.smul_yas.dto.offres_dto.OffreResponseDto;
import com.maxime.smul_yas.entity.Offres;
import com.maxime.smul_yas.entity.UserForfait;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OffresMapper {

    @Mapping(source = "forfaits_id", target = "forfaitsId")
    OffreResponseDto toOffreResponseDto(Offres offres);

    List<OffreResponseDto> toOffreResponseDtoList(List<Offres> offresList);

    @Mapping(source = "offres.forfaits_id", target = "forfaitId")
    @Mapping(source = "offres.appelation", target = "appelation")
    @Mapping(source = "offres.categories", target = "category")
    @Mapping(source = "offres.type", target = "type")
    ActiveForfaitResponseDto toActiveForfaitResponseDto(UserForfait userForfait);

    List<ActiveForfaitResponseDto> toActiveForfaitResponseDtoList(List<UserForfait> userForfaitList);
}
