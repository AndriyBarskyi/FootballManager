package com.task.test.footballmanager.dto;

import lombok.Data;

@Data
public class FootballerDTO {
    private Long id;
    private String name;
    private String surname;
    private Integer age;
    private Integer experience;
    private Long clubId;
}
