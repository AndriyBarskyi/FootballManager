package com.task.test.footballmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.task.test.footballmanager.dto.FootballClubDTO;
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
        @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(
            footballClubService.getFootballClubById(id));
    }

    @PostMapping
    public ResponseEntity<FootballClubDTO> addNewFootballClub(
        @RequestBody FootballClubDTO newFootballClub) {
        return ResponseEntity.status(HttpStatus.OK).body(
            footballClubService.addNewFootballClub(newFootballClub));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFootballClub(@PathVariable Long id) {
        footballClubService.deleteFootballClub(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<FootballClubDTO> updateFootballClub(
        @RequestBody FootballClubDTO newFootballClub, @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(
            footballClubService.updateFootballClub(newFootballClub, id));
    }
}
