package com.task.test.footballmanager.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;

import com.task.test.footballmanager.dto.FootballClubDTO;
import com.task.test.footballmanager.entity.FootballClub;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true), nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface FootballClubMapper {

    FootballClub dtoToEntity(FootballClubDTO commentDTO);

    FootballClubDTO entityToDto(FootballClub comment);

    void updateFootballClub(@MappingTarget FootballClub footballClubFromDB,
        FootballClubDTO newFootballClub);
}
