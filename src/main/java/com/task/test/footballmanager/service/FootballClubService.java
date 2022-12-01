package com.task.test.footballmanager.service;

import com.task.test.footballmanager.dto.FootballClubDTO;

public interface FootballClubService {

    FootballClubDTO getFootballClubById(Long id);

    void deleteFootballClub(Long id);

    FootballClubDTO updateFootballClub(FootballClubDTO newFootballClub,
        Long id);

    FootballClubDTO addNewFootballClub(FootballClubDTO newFootballClub);

}
