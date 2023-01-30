package com.task.test.footballmanager.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

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

    public static Integer calculateAge(LocalDate birthDate) {
        if ((birthDate != null)) {
            return Period.between(birthDate, LocalDate.now()).getYears();
        } else {
            return 0;
        }
    }

    public static Integer calculateExperience(LocalDate birthDate,
        LocalDate careerStart) {
        if (birthDate != null && careerStart != null) {
            return Period.between(birthDate, careerStart).getMonths();
        } else {
            return 0;
        }
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

    @Override
    public FootballerSaveDTO updateFootballer(FootballerSaveDTO newFootballer,
        String id) {
        checkThatFootballerExists(id);
        checkThatAgeIsValid(newFootballer.getDateOfBirth());
        if (newFootballer.getClubId() != null) {
            checkThatFootballClubExists(newFootballer.getClubId());
        }
        checkThatExperienceIsValid(newFootballer.getCareerStartDate(),
            newFootballer.getDateOfBirth());
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
        checkThatAgeIsValid(newFootballer.getDateOfBirth());
        checkThatExperienceIsValid(newFootballer.getCareerStartDate(),
            newFootballer.getDateOfBirth());
        if (newFootballer.getClubId() != null) {
            checkThatFootballClubExists(newFootballer.getClubId());
        }
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

        checkThatFootballClubExists(newClubId);
        FootballClub clubTo =
            footballClubRepository.getReferenceById(newClubId);

        if (footballer.getFootballClub() == null) {
            footballer.setFootballClub(
                footballClubRepository.getReferenceById(newClubId));
            footballerRepository.save(footballer);
            return footballerMapper.entityToDto(footballer);
        }
        checkThatFootballClubExists(footballer.getFootballClub().getId());
        FootballClub clubFrom = footballer.getFootballClub();
        checkThatClubsAreDifferent(clubFrom.getId(), clubTo.getId());

        BigDecimal finalTransferCost =
            calculateTransferCost(footballer.getDateOfBirth(),
                footballer.getCareerStartDate()).subtract(
                calculateTransferCost(footballer.getDateOfBirth(),
                    footballer.getCareerStartDate()).multiply(
                        BigDecimal.valueOf(clubFrom.getCommission()))
                    .divide(BigDecimal.valueOf(100L), new MathContext(2)));

        checkThatClubCanPay(clubTo, finalTransferCost);
        clubFrom.setBalance(clubFrom.getBalance().add(finalTransferCost));
        clubTo.setBalance(clubTo.getBalance().subtract(finalTransferCost));
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

    private void checkThatClubsAreDifferent(String clubFromId,
        String clubToId) {
        if (Objects.equals(clubFromId,
            clubToId)) {
            throw new InvalidTransferException(SAME_CLUB_TRANSFER);
        }
    }

    private BigDecimal calculateTransferCost(LocalDate dateOfBirth,
        LocalDate careerStart) {
        final int TRANSFER_DEFAULT_SUM = 100000;
        return BigDecimal.valueOf(
                (long) calculateExperience(dateOfBirth, careerStart)
                    * TRANSFER_DEFAULT_SUM)
            .divide(BigDecimal.valueOf(calculateAge(dateOfBirth)),
                new MathContext(2));
    }

    private void checkThatExperienceIsValid(LocalDate careerStart,
        LocalDate dateOfBirth) {
        if (dateOfBirth.isAfter(careerStart) || careerStart.isAfter(
            LocalDate.now())) {
            throw new InvalidEntityException(
                INVALID_FOOTBALLER_WITH + " career start date equal to "
                    + careerStart
            );
        }
    }

    private void checkThatAgeIsValid(LocalDate dateOfBirth) {
        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new InvalidEntityException(
                INVALID_FOOTBALLER_WITH + " date of birth equal to "
                    + dateOfBirth);
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
