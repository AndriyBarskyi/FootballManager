package com.task.test.footballmanager.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FootballClubSaveDTO {
    @NotNull
    private String name;
    @NotNull
    private Double commission;
    @NotNull(message = "cannot create club without balance")
    private BigDecimal balance;
}
