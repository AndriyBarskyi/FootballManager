package com.task.test.footballmanager.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Past;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "FOOTBALLER")
public class Footballer {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    @Past
    @Column(nullable = false)
    private LocalDate dateOfBirth;
    @Past
    @Column(nullable = false)
    private LocalDate careerStartDate;

    @ManyToOne
    @JoinColumn(name = "football_club_id", foreignKey = @ForeignKey(name = "fk_footballer_football_club"))
    private FootballClub footballClub;
}
