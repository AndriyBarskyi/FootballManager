package com.task.test.footballmanager.controller;

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

import com.task.test.footballmanager.dto.FootballerDTO;
import com.task.test.footballmanager.dto.FootballerSaveDTO;
import com.task.test.footballmanager.service.FootballerService;

@RestController
@RequestMapping("/api/v1/footballers")
public class FootballerController {
    private final FootballerService footballerService;

    @Autowired
    public FootballerController(FootballerService footballerService) {
        this.footballerService = footballerService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<FootballerDTO> getFootballerById(
        @PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(
            footballerService.getFootballerById(id));
    }

    @PostMapping
    public ResponseEntity<FootballerSaveDTO> addNewFootballer(
        @RequestBody @Validated FootballerSaveDTO newFootballer) {
        return ResponseEntity.status(HttpStatus.OK).body(
            footballerService.addNewFootballer(newFootballer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFootballer(@PathVariable String id) {
        footballerService.deleteFootballer(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<FootballerSaveDTO> updateFootballer(
        @RequestBody @Validated FootballerSaveDTO newFootballer,
        @PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(
            footballerService.updateFootballer(newFootballer, id));
    }

    @PutMapping("/{footballerId}/footballClubs/{footballClubId}")
    public ResponseEntity<FootballerDTO> transferFootballer(
        @PathVariable String footballerId,
        @PathVariable String footballClubId) {
        return ResponseEntity.status(HttpStatus.OK).body(
            footballerService.transferFootballer(footballerId, footballClubId));
    }
}
