package com.task.test.footballmanager.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.task.test.footballmanager.entity.Footballer;

public interface FootballerRepository extends JpaRepository<Footballer, String> {

}