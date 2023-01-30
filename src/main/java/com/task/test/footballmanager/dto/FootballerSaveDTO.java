package com.task.test.footballmanager.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FootballerSaveDTO {
    @NotNull
    private String name;
    @NotNull
    private String surname;
    @NotNull
    private LocalDate dateOfBirth;
    @NotNull
    private LocalDate careerStartDate;
    private String clubId;
}
