package com.task.test.footballmanager.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class FootballerDTO {
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String surname;
    @NotNull
    private Integer age;
    @NotNull
    private Integer experience;
    private Long clubId;
}
