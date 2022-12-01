package com.task.test.footballmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.task.test.footballmanager.entity.FootballClub;

public interface FootballClubRepository extends JpaRepository<FootballClub, Long> {

}
