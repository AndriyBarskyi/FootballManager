package com.task.test.footballmanager.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;

import com.task.test.footballmanager.dto.FootballClubDTO;
import com.task.test.footballmanager.dto.FootballClubSaveDTO;
import com.task.test.footballmanager.entity.FootballClub;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true), nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface FootballClubMapper {

    FootballClub dtoToEntity(FootballClubDTO footballClubDTO);

    FootballClubDTO entityToDto(FootballClub footballClub);

    FootballClub saveDtoToEntity(FootballClubSaveDTO footballClubSaveDTO);

    FootballClubSaveDTO entityToSaveDto(FootballClub footballClub);

    void updateFootballClub(@MappingTarget FootballClub footballClubFromDB,
        FootballClubSaveDTO newFootballClub);
}
