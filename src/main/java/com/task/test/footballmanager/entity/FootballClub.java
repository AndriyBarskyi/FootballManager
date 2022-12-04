package com.task.test.footballmanager.entity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FOOTBALL_CLUB")
public class FootballClub {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "commission", nullable = false)
    private Integer commission;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "footballClub", cascade = CascadeType.PERSIST)
    @EqualsAndHashCode.Exclude
    private List<Footballer> footballers;

    @PreRemove
    private void preRemove() {
        footballers.forEach(footballer -> footballer.setFootballClub(null));
    }
}
