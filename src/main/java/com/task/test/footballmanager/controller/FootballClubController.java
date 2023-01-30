package com.task.test.footballmanager.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.task.test.footballmanager.dto.FootballClubDTO;
import com.task.test.footballmanager.dto.FootballClubSaveDTO;
import com.task.test.footballmanager.service.FootballClubService;

@RestController
@RequestMapping("/api/v1/football-clubs")
public class FootballClubController {

    private final FootballClubService footballClubService;

    @Autowired
    public FootballClubController(FootballClubService footballClubService) {
        this.footballClubService = footballClubService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<FootballClubDTO> getFootballClubById(
        @PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(
            footballClubService.getFootballClubById(id));
    }

    @PostMapping
    public ResponseEntity<FootballClubSaveDTO> addNewFootballClub(
        @RequestBody @Validated FootballClubSaveDTO newFootballClub) {
        return ResponseEntity.status(HttpStatus.OK).body(
            footballClubService.addNewFootballClub(newFootballClub));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFootballClub(@PathVariable String id) {
        footballClubService.deleteFootballClub(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<FootballClubSaveDTO> updateFootballClub(
        @RequestBody @Valid FootballClubSaveDTO newFootballClub,
        @PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(
            footballClubService.updateFootballClub(newFootballClub, id));
    }
}
