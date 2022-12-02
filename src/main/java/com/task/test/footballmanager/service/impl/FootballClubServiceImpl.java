package com.task.test.footballmanager.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.task.test.footballmanager.dto.FootballClubDTO;
import com.task.test.footballmanager.exception.EntityAlreadyExistsException;
import com.task.test.footballmanager.exception.EntityNotExistsException;
import com.task.test.footballmanager.exception.InvalidEntityException;
import com.task.test.footballmanager.mapper.FootballClubMapper;
import com.task.test.footballmanager.repository.FootballClubRepository;
import com.task.test.footballmanager.service.FootballClubService;

@Service
public class FootballClubServiceImpl implements FootballClubService {
    private static final String FOOTBALL_CLUB_NOT_FOUND_BY_ID =
        "Football club not found by id: ";
    private static final String FOOTBALL_CLUB_ALREADY_EXISTS_BY_ID =
        "Football club already exists by id: ";
    private static final String INVALID_FOOTBALL_CLUB_WITH =
        "Cannot add football club with: ";
    private final FootballClubRepository footballClubRepository;
    private final FootballClubMapper footballClubMapper;

    @Autowired
    public FootballClubServiceImpl(
        FootballClubRepository footballClubRepository,
        FootballClubMapper footballClubMapper) {
        this.footballClubMapper = footballClubMapper;
        this.footballClubRepository = footballClubRepository;
    }

    @Override
    public FootballClubDTO getFootballClubById(Long id) {
        checkThatFootballClubExists(id);
        return footballClubMapper.entityToDto(footballClubRepository
            .getReferenceById(id));
    }

    @Override
    public void deleteFootballClub(Long id) {
        checkThatFootballClubExists(id);
        footballClubRepository.deleteById(id);
    }

    @Override
    public FootballClubDTO updateFootballClub(FootballClubDTO newFootballClub,
        Long id) {
        checkThatFootballClubExists(newFootballClub.getId());
        checkThatClubBalanceIsValid(newFootballClub.getBalance());
        checkThatClubCommissionIsValid(newFootballClub.getCommission());
        return footballClubRepository.findById(id)
            .map(footballClub -> {
                footballClubMapper.updateFootballClub(footballClub,
                    newFootballClub);
                return footballClubMapper
                    .entityToDto(footballClubRepository
                        .save(footballClub));
            }).orElseThrow(EntityNotExistsException::new);
    }

    @Override
    public FootballClubDTO addNewFootballClub(FootballClubDTO newFootballClub) {
        checkThatFootballClubNotExists(newFootballClub.getId());
        checkThatClubBalanceIsValid(newFootballClub.getBalance());
        checkThatClubCommissionIsValid(newFootballClub.getCommission());
        return footballClubMapper.entityToDto(
            footballClubRepository.save(
                footballClubMapper.dtoToEntity(newFootballClub))
        );
    }

    private void checkThatFootballClubNotExists(Long id) {
        if (footballClubRepository.existsById(id)) {
            throw new EntityAlreadyExistsException(
                FOOTBALL_CLUB_ALREADY_EXISTS_BY_ID + id);
        }
    }

    private void checkThatFootballClubExists(Long id) {
        if (!footballClubRepository.existsById(id)) {
            throw new EntityNotExistsException(
                FOOTBALL_CLUB_NOT_FOUND_BY_ID + id);
        }
    }

    private void checkThatClubBalanceIsValid(BigDecimal balance) {
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidEntityException(
                INVALID_FOOTBALL_CLUB_WITH + "balance " + balance);
        }
    }

    private void checkThatClubCommissionIsValid(Integer commission) {
        if (commission < 0 || commission > 10) {
            throw new InvalidEntityException(
                INVALID_FOOTBALL_CLUB_WITH + "commission " + commission);
        }
    }
}
