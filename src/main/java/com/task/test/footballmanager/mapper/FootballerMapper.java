package com.task.test.footballmanager.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;

import com.task.test.footballmanager.dto.FootballerDTO;
import com.task.test.footballmanager.dto.FootballerSaveDTO;
import com.task.test.footballmanager.entity.Footballer;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true), nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface FootballerMapper {
    @Mapping(target = "footballClub.id", source = "clubId")
    Footballer dtoToEntity(FootballerDTO footballerDTO);

    @Mapping(target = "clubId", source = "footballClub.id")
    FootballerDTO entityToDto(Footballer footballer);

    @Mapping(target = "footballClub.id", source = "clubId")
    Footballer saveDtoToEntity(FootballerSaveDTO footballerSaveDTO);

    @Mapping(target = "clubId", source = "footballClub.id")
    FootballerSaveDTO entityToSaveDto(Footballer footballer);

    @Mapping(target = "footballClub.id", source = "clubId")
    void updateFootballer(@MappingTarget Footballer footballerFromDB,
        FootballerSaveDTO newFootballer);
}
