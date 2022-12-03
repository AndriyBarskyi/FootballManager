package com.task.test.footballmanager.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class FootballClubSaveDTO {
    @NotNull
    private String name;
    @NotNull
    private Integer commission;
    @NotNull
    private BigDecimal balance;
}
