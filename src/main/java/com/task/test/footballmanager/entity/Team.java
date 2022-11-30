package com.task.test.footballmanager.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TEAM")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "commission")
    private Integer commission;

    @Column(name = "balance")
    private Long balance;
}
