package com.task.test.footballmanager.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FootballerServiceTest {
    public static final String INSUFFICIENT_FUNDS =
        "Insufficient funds to commit the transfer for ";
    public static final String SAME_CLUB_TRANSFER =
        "Cannot commit the transfer to the same club";
    private static final String INVALID_FOOTBALLER_WITH =
        "Cannot add footballer with: ";
    private static final String FOOTBALLER_NOT_FOUND_BY_ID =
        "Footballer not found by id: ";
    private static final String FOOTBALL_CLUB_NOT_FOUND_BY_ID =
        "Football club not found by id: ";
    @Mock
    private FootballerRepository footballerRepository;
    @Mock
    private FootballClubRepository footballClubRepository;
    @Spy
    private FootballerMapper footballerMapper =
        Mappers.getMapper(FootballerMapper.class);
    @InjectMocks
    private FootballerServiceImpl underTest;

    private static Stream<Arguments> invalidExperienceDataProvider() {
        return Stream.of(
            Arguments.of(LocalDate.of(2008, 11, 6), LocalDate.of(1992, 6, 16)),
            Arguments.of(LocalDate.of(1992, 11, 6), LocalDate.of(1992, 11, 5)),
            Arguments.of(LocalDate.of(1992, 11, 6), LocalDate.now().plusDays(1)));
    }

    @Test
    void canAddNewFootballerWithoutTheClub() {
        Footballer validFootballer =
            new Footballer("123", "John", "Doe", LocalDate.of(1992, 11, 6),
                LocalDate.of(2008, 6, 16), null);
        FootballerSaveDTO validFootballerSaveDTO =
            new FootballerSaveDTO("John", "Doe", LocalDate.of(1992, 11, 6),
                LocalDate.of(2008, 6, 16), null);
        when(footballerMapper.saveDtoToEntity(any()))
            .thenReturn(validFootballer);

        underTest.addNewFootballer(validFootballerSaveDTO);
        ArgumentCaptor<Footballer> footballerArgumentCaptor =
            ArgumentCaptor.forClass(Footballer.class);
        verify(footballerRepository).save(
            footballerArgumentCaptor.capture());
        Footballer capturedFootballer =
            footballerArgumentCaptor.getValue();
        assertThat(capturedFootballer).isEqualTo(
            validFootballer);
    }

    @Test
    void canAddNewFootballerWithTheClub() {
        Footballer validFootballer =
            new Footballer("123", "John", "Doe", LocalDate.of(1992, 11, 6),
                LocalDate.of(2008, 6, 16),
                new FootballClub("123", "Name", 4.3, BigDecimal.TEN));
        FootballerSaveDTO validFootballerSaveDTO =
            new FootballerSaveDTO("John", "Doe", LocalDate.of(1992, 11, 6),
                LocalDate.of(2008, 6, 16), "123");
        when(footballerMapper.saveDtoToEntity(any()))
            .thenReturn(validFootballer);
        when(footballClubRepository.existsById(anyString())).thenReturn(true);

        underTest.addNewFootballer(validFootballerSaveDTO);
        ArgumentCaptor<Footballer> footballerArgumentCaptor =
            ArgumentCaptor.forClass(Footballer.class);
        verify(footballerRepository).save(
            footballerArgumentCaptor.capture());
        Footballer capturedFootballer =
            footballerArgumentCaptor.getValue();
        assertThat(capturedFootballer).isEqualTo(
            validFootballer);
    }

    void cannotAddFootballerWithInvalidAge() {
        FootballerSaveDTO footballerDTO =
            new FootballerSaveDTO("Anton", "Bobyk", LocalDate.now().plusDays(1),
                LocalDate.of(2008, 6, 16), null);

        assertThatThrownBy(
            () -> underTest.addNewFootballer(footballerDTO))
            .isInstanceOf(InvalidEntityException.class)
            .hasMessageContaining(INVALID_FOOTBALLER_WITH);
        verify(footballerRepository, never()).save(any(Footballer.class));

    }

    @ParameterizedTest
    @MethodSource("invalidExperienceDataProvider")
    void cannotAddFootballerWithInvalidExperience(LocalDate dateOfBirth,
        LocalDate careerStartDate) {
        FootballerSaveDTO footballerDTO =
            new FootballerSaveDTO("Anton", "Bobyk", dateOfBirth,
                careerStartDate, null);

        assertThatThrownBy(
            () -> underTest.addNewFootballer(footballerDTO))
            .isInstanceOf(InvalidEntityException.class)
            .hasMessageContaining(INVALID_FOOTBALLER_WITH);
        verify(footballerRepository, never()).save(any(Footballer.class));

    }

    @Test
    void cannotAddFootballerWithNotExistingClub() {
        FootballerSaveDTO footballerDTO =
            new FootballerSaveDTO("Anton", "Bobyk", LocalDate.of(1992, 11, 6),
                LocalDate.of(2008, 6, 16), "1234");

        assertThatThrownBy(
            () -> underTest.addNewFootballer(footballerDTO))
            .isInstanceOf(EntityNotExistsException.class)
            .hasMessageContaining(FOOTBALL_CLUB_NOT_FOUND_BY_ID);
        verify(footballerRepository, never()).save(any(Footballer.class));

    }

    @Test
    void canGetFootballer() {
        Footballer validFootballer =
            new Footballer("123", "John", "Doe", LocalDate.of(1992, 11, 6),
                LocalDate.of(2008, 6, 16), null);
        FootballerDTO validFootballerDTO =
            new FootballerDTO("123", "John", "Doe", LocalDate.of(1992, 11, 6),
                LocalDate.of(2008, 6, 16), null);
        when(footballerRepository.getReferenceById(any()))
            .thenReturn(validFootballer);
        when(footballerRepository.existsById(any())).thenReturn(true);
        when(footballerMapper.entityToDto(any()))
            .thenReturn(validFootballerDTO);

        FootballerDTO returnedFootballerDTO =
            underTest.getFootballerById(anyString());
        assertThat(returnedFootballerDTO).isEqualTo(validFootballerDTO);
    }

    @Test
    void canUpdateFootballer() {
        Footballer validFootballer =
            new Footballer("123", "John", "Doe", LocalDate.of(1992, 11, 6),
                LocalDate.of(2008, 6, 16),
                new FootballClub("1234", "Name", 4.25, BigDecimal.TEN));
        FootballerSaveDTO validFootballerSaveDTO =
            new FootballerSaveDTO("John", "Doe", LocalDate.of(1992, 11, 6),
                LocalDate.of(2008, 6, 16), "1234");
        when(footballerMapper.entityToSaveDto(any()))
            .thenReturn(validFootballerSaveDTO);
        when(footballerRepository.findById(any()))
            .thenReturn(Optional.of(validFootballer));
        doCallRealMethod().when(footballerMapper)
            .updateFootballer(validFootballer, validFootballerSaveDTO);
        when(footballerRepository.existsById(anyString())).thenReturn(true);
        when(footballClubRepository.existsById(anyString())).thenReturn(true);

        underTest.updateFootballer(validFootballerSaveDTO, anyString());
        ArgumentCaptor<Footballer> footballerArgumentCaptor =
            ArgumentCaptor.forClass(Footballer.class);
        verify(footballerRepository).save(
            footballerArgumentCaptor.capture());
        Footballer capturedFootballer =
            footballerArgumentCaptor.getValue();
        assertThat(capturedFootballer).isEqualTo(validFootballer);
    }

    @Test
    void cannotDeleteNotExistingFootballer() {
        String id = anyString();
        when(footballerRepository.existsById(id))
            .thenReturn(false);
        assertThatThrownBy(() -> underTest.deleteFootballer(id))
            .isInstanceOf(EntityNotExistsException.class)
            .hasMessageContaining(FOOTBALLER_NOT_FOUND_BY_ID);
        verify(footballerRepository, never()).delete(any(Footballer.class));
    }

    @Test
    void canCommitTheTransfer() {
        FootballClub clubFrom =
            new FootballClub("12345", "Name", 4.55,
                BigDecimal.valueOf(100000000));
        FootballClub clubTo =
            new FootballClub("12347", "Name", 4.1,
                BigDecimal.valueOf(200000000));
        Footballer validFootballer =
            new Footballer("123", "John", "Doe", LocalDate.of(1992, 11, 6),
                LocalDate.of(2008, 6, 16),
                clubFrom);
        FootballerDTO validFootballerDTO =
            new FootballerDTO("123", "John", "Doe", LocalDate.of(1992, 11, 6),
                LocalDate.of(2008, 6, 16), clubTo.getId());

        when(footballerRepository.getReferenceById(validFootballer.getId()))
            .thenReturn(validFootballer);
        when(footballClubRepository.getReferenceById(
            clubTo.getId())).thenReturn(clubTo);
        when(footballerMapper.entityToDto(any()))
            .thenReturn(validFootballerDTO);
        when(footballerRepository.existsById(anyString())).thenReturn(true);
        when(footballClubRepository.existsById(anyString())).thenReturn(true);

        FootballerDTO returnedFootballer =
            underTest.transferFootballer(validFootballer.getId(),
                clubTo.getId());

        assertThat(returnedFootballer).isEqualTo(validFootballerDTO);
    }

    @Test
    void canCommitTheFreeAgentTransfer() {
        FootballClub clubTo =
            new FootballClub("12347", "Name", 4.4,
                BigDecimal.valueOf(200000000));
        Footballer validFootballer =
            new Footballer("123", "John", "Doe", LocalDate.of(1992, 11, 6),
                LocalDate.of(2008, 6, 16),
                null);
        FootballerDTO validFootballerDTO =
            new FootballerDTO("123", "John", "Doe", LocalDate.of(1992, 11, 6),
                LocalDate.of(2008, 6, 16), clubTo.getId());

        when(footballerRepository.getReferenceById(validFootballer.getId()))
            .thenReturn(validFootballer);
        when(footballClubRepository.getReferenceById(
            clubTo.getId())).thenReturn(clubTo);
        when(footballerMapper.entityToDto(any()))
            .thenReturn(validFootballerDTO);
        when(footballerRepository.existsById(anyString())).thenReturn(true);
        when(footballClubRepository.existsById(anyString())).thenReturn(true);

        FootballerDTO returnedFootballer =
            underTest.transferFootballer(validFootballer.getId(),
                clubTo.getId());

        assertThat(returnedFootballer).isEqualTo(validFootballerDTO);
    }

    @Test
    void cannotCommitTheTransferToTheClubThatCannotPayForIt() {
        FootballClub clubFrom =
            new FootballClub("12345", "Name", 4.6,
                BigDecimal.valueOf(100000000));
        FootballClub clubTo =
            new FootballClub("12347", "Name", 4.1, BigDecimal.valueOf(2000));
        Footballer validFootballer =
            new Footballer("123", "John", "Doe", LocalDate.of(1992, 11, 6),
                LocalDate.of(2008, 6, 16),
                clubFrom);

        when(footballerRepository.getReferenceById(validFootballer.getId()))
            .thenReturn(validFootballer);
        when(footballClubRepository.getReferenceById(
            clubTo.getId())).thenReturn(clubTo);
        when(footballerRepository.existsById(anyString())).thenReturn(true);
        when(footballClubRepository.existsById(anyString())).thenReturn(true);

        assertThatThrownBy(
            () -> underTest.transferFootballer(validFootballer.getId(),
                clubTo.getId()))
            .isInstanceOf(InvalidTransferException.class)
            .hasMessageContaining(INSUFFICIENT_FUNDS);
        verify(footballerRepository, never()).save(any(Footballer.class));
    }

    @Test
    void cannotCommitTheTransferToTheSameClub() {
        FootballClub clubFrom =
            new FootballClub("12345", "Name", 4.2,
                BigDecimal.valueOf(100000000));
        Footballer validFootballer =
            new Footballer("123", "John", "Doe", LocalDate.of(1992, 11, 6),
                LocalDate.of(2008, 6, 16),
                clubFrom);

        when(footballerRepository.getReferenceById(validFootballer.getId()))
            .thenReturn(validFootballer);
        when(footballClubRepository.getReferenceById(
            clubFrom.getId())).thenReturn(clubFrom);
        when(footballerRepository.existsById(anyString())).thenReturn(true);
        when(footballClubRepository.existsById(anyString())).thenReturn(true);

        assertThatThrownBy(
            () -> underTest.transferFootballer(validFootballer.getId(),
                clubFrom.getId()))
            .isInstanceOf(InvalidTransferException.class)
            .hasMessageContaining(SAME_CLUB_TRANSFER);
        verify(footballerRepository, never()).save(any(Footballer.class));
    }
}
