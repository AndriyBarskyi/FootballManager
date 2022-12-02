package com.task.test.footballmanager.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.task.test.footballmanager.dto.FootballerDTO;
import com.task.test.footballmanager.entity.FootballClub;
import com.task.test.footballmanager.entity.Footballer;
import com.task.test.footballmanager.exception.EntityAlreadyExistsException;
import com.task.test.footballmanager.exception.EntityNotExistsException;
import com.task.test.footballmanager.exception.InsufficientFundsException;
import com.task.test.footballmanager.exception.InvalidEntityException;
import com.task.test.footballmanager.mapper.FootballerMapper;
import com.task.test.footballmanager.repository.FootballClubRepository;
import com.task.test.footballmanager.repository.FootballerRepository;
import com.task.test.footballmanager.service.FootballerService;

@Service
public class FootballerServiceImpl implements FootballerService {

    private static final String FOOTBALLER_NOT_FOUND_BY_ID =
        "Footballer not found by id: ";
    private static final String FOOTBALL_CLUB_NOT_FOUND_BY_ID =
        "Football club not found by id: ";
    private static final String FOOTBALLER_ALREADY_EXISTS_BY_ID =
        "Footballer already exists by id: ";
    private static final String INVALID_FOOTBALLER_WITH =
        "Cannot add footballer with: ";

    private final FootballerRepository footballerRepository;
    private final FootballerMapper footballerMapper;
    private final FootballClubRepository footballClubRepository;

    @Autowired
    public FootballerServiceImpl(FootballerRepository footballerRepository,
        FootballClubRepository footballClubRepository,
        FootballerMapper footballerMapper) {
        this.footballerRepository = footballerRepository;
        this.footballerMapper = footballerMapper;
        this.footballClubRepository = footballClubRepository;
    }

    @Override public FootballerDTO getFootballerById(Long id) {
        checkThatFootballerExists(id);
        return footballerMapper.entityToDto(footballerRepository
            .getReferenceById(id));
    }

    @Override public void deleteFootballer(Long id) {
        checkThatFootballerExists(id);
        footballerRepository.deleteById(id);
    }

    @Override public FootballerDTO updateFootballer(FootballerDTO newFootballer,
        Long id) {
        checkThatFootballerExists(id);
        checkThatAgeIsValid(newFootballer.getAge());
        checkThatFootballClubExists(newFootballer.getClubId());
        checkThatExperienceIsValid(newFootballer.getExperience());
        return footballerRepository.findById(id)
            .map(footballer -> {
                footballerMapper.updateFootballer(footballer, newFootballer);
                return footballerMapper
                    .entityToDto(footballerRepository
                        .save(footballer));
            }).orElseThrow(EntityNotExistsException::new);
    }

    @Override
    public FootballerDTO addNewFootballer(FootballerDTO newFootballer) {
        checkThatFootballerNotExists(newFootballer.getId());
        checkThatAgeIsValid(newFootballer.getAge());
        checkThatFootballClubExists(newFootballer.getClubId());
        checkThatExperienceIsValid(newFootballer.getExperience());
        return footballerMapper.entityToDto(
            footballerRepository.save(
                footballerMapper.dtoToEntity(newFootballer))
        );
    }

    @Override
    @Transactional
    public FootballerDTO transferFootballer(Long id, Long newClubId) {
        Footballer footballer = footballerRepository.getReferenceById(id);
        FootballClub clubFrom = footballer.getFootballClub();

        BigDecimal transferCost = calculateTransferCost(footballer.getAge(),
            footballer.getExperience()).multiply(
            BigDecimal.valueOf(clubFrom.getCommission())
                .divide(BigDecimal.valueOf(100L), new MathContext(2)));

        FootballClub clubTo =
            footballClubRepository.getReferenceById(newClubId);
        if (clubTo.getBalance().compareTo(transferCost) < 1) {
            throw new InsufficientFundsException();
        }
        clubFrom.setBalance(clubFrom.getBalance().subtract(transferCost));
        clubTo.setBalance(clubTo.getBalance().add(transferCost));
        footballer.setFootballClub(clubTo);
        footballClubRepository.save(clubFrom);
        footballClubRepository.save(clubTo);
        footballerRepository.save(footballer);
        return footballerMapper.entityToDto(footballer);
    }

    private BigDecimal calculateTransferCost(Integer age, Integer exp) {
        final int TRANSFER_DEFAULT_SUM = 100000;
        return BigDecimal.valueOf((long) exp * TRANSFER_DEFAULT_SUM)
            .divide(BigDecimal.valueOf(age),
                new MathContext(2));
    }

    private void checkThatExperienceIsValid(Integer experience) {
        if (experience < 0 || experience > 600) {
            throw new InvalidEntityException(
                INVALID_FOOTBALLER_WITH + " experience equal to " + experience
            );
        }
    }

    private void checkThatAgeIsValid(Integer age) {
        if (age < 10 || age > 60) {
            throw new InvalidEntityException(
                INVALID_FOOTBALLER_WITH + "age equal to " + age);
        }
    }

    private void checkThatFootballerNotExists(Long id) {
        if (footballerRepository.existsById(id)) {
            throw new EntityAlreadyExistsException(
                FOOTBALLER_ALREADY_EXISTS_BY_ID + id);
        }
    }

    private void checkThatFootballerExists(Long id) {
        if (!footballerRepository.existsById(id)) {
            throw new EntityNotExistsException(FOOTBALLER_NOT_FOUND_BY_ID + id);
        }
    }

    private void checkThatFootballClubExists(Long id) {
        if (!footballClubRepository.existsById(id)) {
            throw new EntityNotExistsException(
                FOOTBALL_CLUB_NOT_FOUND_BY_ID + id);
        }
    }
}
