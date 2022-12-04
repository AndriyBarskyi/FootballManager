package com.task.test.footballmanager.service.impl;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.task.test.footballmanager.dto.FootballClubDTO;
import com.task.test.footballmanager.dto.FootballClubSaveDTO;
import com.task.test.footballmanager.entity.FootballClub;
import com.task.test.footballmanager.exception.EntityNotExistsException;
import com.task.test.footballmanager.exception.InvalidEntityException;
import com.task.test.footballmanager.mapper.FootballClubMapper;
import com.task.test.footballmanager.repository.FootballClubRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FootballClubServiceTest {
    private static final String INVALID_FOOTBALL_CLUB_WITH =
        "Cannot add football club with: ";
    private static final String FOOTBALL_CLUB_NOT_FOUND_BY_ID =
        "Football club not found by id: ";
    @Mock
    private FootballClubRepository footballClubRepository;
    @Spy
    private FootballClubMapper footballClubMapper =
        Mappers.getMapper(FootballClubMapper.class);
    @InjectMocks
    private FootballClubServiceImpl underTest;

    private static Stream<Integer> invalidCommissionDataProvider() {
        return Stream.of(-5, -1, 11, 25);
    }

    private static Stream<BigDecimal> invalidBalanceDataProvider() {
        return Stream.of(BigDecimal.valueOf(-5L), BigDecimal.valueOf(-1L));
    }

    @Test
    void canAddNewFootballClub() {
        String id = "123";
        String name = "Lviv";
        BigDecimal balance = BigDecimal.TEN;
        Integer commission = 2;
        FootballClubSaveDTO footballClubDTO =
            new FootballClubSaveDTO(name, commission, balance);
        FootballClub footballClub =
            new FootballClub(id, name, commission, balance);

        when(footballClubMapper.saveDtoToEntity(any()))
            .thenReturn(footballClub);

        underTest.addNewFootballClub(footballClubDTO);
        ArgumentCaptor<FootballClub> footballClubArgumentCaptor =
            ArgumentCaptor.forClass(FootballClub.class);
        verify(footballClubRepository).save(
            footballClubArgumentCaptor.capture());
        FootballClub capturedFootballClub =
            footballClubArgumentCaptor.getValue();
        assertThat(capturedFootballClub).isEqualTo(
            footballClub);
    }

    @ParameterizedTest
    @MethodSource("invalidCommissionDataProvider")
    void cannotAddFootballClubWithInvalidCommission(Integer commission) {
        FootballClubSaveDTO footballClubDTO =
            new FootballClubSaveDTO("Lviv", commission, BigDecimal.TEN);

        assertThatThrownBy(
            () -> underTest.addNewFootballClub(footballClubDTO))
            .isInstanceOf(InvalidEntityException.class)
            .hasMessageContaining(INVALID_FOOTBALL_CLUB_WITH);
        verify(footballClubRepository, never()).save(any(FootballClub.class));

    }

    @ParameterizedTest
    @MethodSource("invalidBalanceDataProvider")
    void cannotAddFootballClubWithInvalidBalance(BigDecimal balance) {
        FootballClubSaveDTO footballClubDTO =
            new FootballClubSaveDTO("Lviv", 5, balance);

        assertThatThrownBy(
            () -> underTest.addNewFootballClub(footballClubDTO))
            .isInstanceOf(InvalidEntityException.class)
            .hasMessageContaining(INVALID_FOOTBALL_CLUB_WITH);
        verify(footballClubRepository, never()).save(any(FootballClub.class));

    }

    @Test
    void canGetFootballClub() {
        String id = "1";
        String name = "Name";
        Integer commission = 5;
        BigDecimal balance = BigDecimal.TEN;
        FootballClub footballClubFromDB =
            new FootballClub(id, name, commission, balance);
        FootballClubDTO footballClubDTO =
            new FootballClubDTO(id, name, commission, balance);

        when(footballClubRepository.getReferenceById(any()))
            .thenReturn(footballClubFromDB);
        when(footballClubRepository.existsById(any())).thenReturn(true);
        when(footballClubMapper.entityToDto(any()))
            .thenReturn(footballClubDTO);

        FootballClubDTO returnedFootballClubDTO =
            underTest.getFootballClubById(id);
        assertThat(returnedFootballClubDTO).isEqualTo(footballClubDTO);
    }

    @Test
    void canUpdateFootballClub() {
        String id = "1";
        String name = "Name";
        BigDecimal balance = BigDecimal.TEN;
        FootballClub footballClubFromDB =
            new FootballClub(id, name, 5, balance);
        footballClubFromDB.setBalance(balance);
        FootballClubSaveDTO newFootballClub =
            new FootballClubSaveDTO(name, 1, balance);

        when(footballClubMapper.entityToSaveDto(any()))
            .thenReturn(newFootballClub);
        when(footballClubRepository.findById(any()))
            .thenReturn(Optional.of(footballClubFromDB));
        doCallRealMethod().when(footballClubMapper)
            .updateFootballClub(footballClubFromDB, newFootballClub);

        underTest.updateFootballClub(newFootballClub, id);
        ArgumentCaptor<FootballClub> footballClubArgumentCaptor =
            ArgumentCaptor.forClass(FootballClub.class);
        verify(footballClubRepository).save(
            footballClubArgumentCaptor.capture());
        FootballClub capturedFootballClub =
            footballClubArgumentCaptor.getValue();
        assertThat(capturedFootballClub).isEqualTo(footballClubFromDB);
    }

    @Test
    void cannotDeleteNotExistingFootballClub() {
        String id = anyString();
        when(footballClubRepository.existsById(id))
            .thenReturn(false);
        assertThatThrownBy(() -> underTest.deleteFootballClub(id))
            .isInstanceOf(EntityNotExistsException.class)
            .hasMessageContaining(FOOTBALL_CLUB_NOT_FOUND_BY_ID);
        verify(footballClubRepository, never()).delete(any(FootballClub.class));
    }
}
