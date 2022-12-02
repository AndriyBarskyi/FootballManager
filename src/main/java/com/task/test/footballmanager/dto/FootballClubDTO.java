package com.task.test.footballmanager.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class FootballClubDTO {
    private Long id;
    private String name;
    private Integer commission;
    private BigDecimal balance;
}
