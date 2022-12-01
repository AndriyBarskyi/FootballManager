package com.task.test.footballmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.task.test.footballmanager.entity.Footballer;

public interface FootballerRepository extends JpaRepository<Footballer, Long> {

}