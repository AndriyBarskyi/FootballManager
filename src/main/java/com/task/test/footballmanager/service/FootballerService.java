package com.task.test.footballmanager.service;

import com.task.test.footballmanager.dto.FootballerDTO;
import com.task.test.footballmanager.dto.FootballerSaveDTO;

public interface FootballerService {

    FootballerDTO getFootballerById(String id);

    void deleteFootballer(String id);

    FootballerSaveDTO updateFootballer(FootballerSaveDTO newFootballer,
        String id);

    FootballerSaveDTO addNewFootballer(FootballerSaveDTO newFootballer);

    FootballerDTO transferFootballer(String id, String newClubId);
}
