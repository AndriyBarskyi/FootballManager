package com.task.test.footballmanager.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;
import java.lang.String;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.task.test.footballmanager.dto.FootballerDTO;
import com.task.test.footballmanager.dto.FootballerSaveDTO;
import com.task.test.footballmanager.entity.FootballClub;
import com.task.test.footballmanager.entity.Footballer;
import com.task.test.footballmanager.exception.EntityNotExistsException;
import com.task.test.footballmanager.exception.InvalidEntityException;
import com.task.test.footballmanager.exception.InvalidTransferException;
import com.task.test.footballmanager.mapper.FootballerMapper;
import com.task.test.footballmanager.repository.FootballClubRepository;
import com.task.test.footballmanager.repository.FootballerRepository;
import com.task.test.footballmanager.service.FootballerService;

@Service
public class FootballerServiceImpl implements FootballerService {
    public static final String SAME_CLUB_TRANSFER =
        "Cannot commit the transfer to the same club";
    public static final String INSUFFICIENT_FUNDS =
        "Insufficient funds to commit the transfer for ";
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

    @Override public FootballerDTO getFootballerById(String id) {
        checkThatFootballerExists(id);
        return footballerMapper.entityToDto(footballerRepository
            .getReferenceById(id));
    }

    @Override public void deleteFootballer(String id) {
        checkThatFootballerExists(id);
        footballerRepository.deleteById(id);
    }

    @Override public FootballerSaveDTO updateFootballer(FootballerSaveDTO newFootballer,
        String id) {
        checkThatFootballerExists(id);
        checkThatAgeIsValid(newFootballer.getAge());
        checkThatFootballClubExists(newFootballer.getClubId());
        checkThatExperienceIsValid(newFootballer.getExperience());
        return footballerRepository.findById(id)
            .map(footballer -> {
                footballerMapper.updateFootballer(footballer, newFootballer);
                return footballerMapper
                    .entityToSaveDto(footballerRepository
                        .save(footballer));
            }).orElseThrow(EntityNotExistsException::new);
    }

    @Override
    public FootballerSaveDTO addNewFootballer(FootballerSaveDTO newFootballer) {
        checkThatAgeIsValid(newFootballer.getAge());
        checkThatFootballClubExists(newFootballer.getClubId());
        checkThatExperienceIsValid(newFootballer.getExperience());
        return footballerMapper.entityToSaveDto(
            footballerRepository.save(
                footballerMapper.saveDtoToEntity(newFootballer))
        );
    }

    @Override
    @Transactional
    public FootballerDTO transferFootballer(String id, String newClubId) {
        checkThatFootballerExists(id);
        Footballer footballer = footballerRepository.getReferenceById(id);

        checkThatFootballClubExists(footballer.getFootballClub().getId());
        FootballClub clubFrom = footballer.getFootballClub();

        checkThatFootballClubExists(newClubId);
        FootballClub clubTo =
            footballClubRepository.getReferenceById(newClubId);
        checkThatClubsAreDifferent(clubFrom.getId(), clubTo.getId());

        BigDecimal transferCost = calculateTransferCost(footballer.getAge(),
            footballer.getExperience()).multiply(
            BigDecimal.valueOf(clubFrom.getCommission())
                .divide(BigDecimal.valueOf(100L), new MathContext(2)));

        checkThatClubCanPay(clubTo, transferCost);
        clubFrom.setBalance(clubFrom.getBalance().subtract(transferCost));
        clubTo.setBalance(clubTo.getBalance().add(transferCost));
        footballer.setFootballClub(clubTo);
        footballClubRepository.save(clubFrom);
        footballClubRepository.save(clubTo);
        footballerRepository.save(footballer);
        return footballerMapper.entityToDto(footballer);
    }

    private void checkThatClubCanPay(FootballClub clubTo,
        BigDecimal transferCost) {
        if (clubTo.getBalance().compareTo(transferCost) < 1) {
            throw new InvalidTransferException(
                INSUFFICIENT_FUNDS + transferCost);
        }
    }

    private void checkThatClubsAreDifferent(String clubFromId, String clubToId) {
        if (Objects.equals(clubFromId,
            clubToId)) {
            throw new InvalidTransferException(SAME_CLUB_TRANSFER);
        }
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

    private void checkThatFootballerExists(String id) {
        if (!footballerRepository.existsById(id)) {
            throw new EntityNotExistsException(FOOTBALLER_NOT_FOUND_BY_ID + id);
        }
    }

    private void checkThatFootballClubExists(String id) {
        if (!footballClubRepository.existsById(id)) {
            throw new EntityNotExistsException(
                FOOTBALL_CLUB_NOT_FOUND_BY_ID + id);
        }
    }
}
