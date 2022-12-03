package com.task.test.footballmanager.service;

import com.task.test.footballmanager.dto.FootballClubDTO;
import com.task.test.footballmanager.dto.FootballClubSaveDTO;

public interface FootballClubService {

    FootballClubDTO getFootballClubById(String id);

    void deleteFootballClub(String id);

    FootballClubSaveDTO updateFootballClub(FootballClubSaveDTO newFootballClub,
        String id);

    FootballClubSaveDTO addNewFootballClub(FootballClubSaveDTO newFootballClub);

}
