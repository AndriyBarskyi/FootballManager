package com.task.test.footballmanager.service;

import com.task.test.footballmanager.dto.FootballerDTO;

public interface FootballerService {

    FootballerDTO getFootballerById(Long id);

    void deleteFootballer(Long id);

    FootballerDTO updateFootballer(FootballerDTO newFootballer, Long id);

    FootballerDTO addNewFootballer(FootballerDTO newFootballer);

    FootballerDTO transferFootballer(Long id, Long newClubId);
}
