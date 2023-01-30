package com.task.test.footballmanager.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FootballClubDTO {
    private String id;
    @NotNull
    private String name;
    @NotNull
    private Double commission;
    @NotNull
    private BigDecimal balance;
}
