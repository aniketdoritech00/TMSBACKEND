package com.doritech.tmsservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.TrainingRequest;
import com.doritech.tmsservice.service.TrainingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tmsService/api/trainings")
public class TrainingController {

    private static final Logger log = LoggerFactory.getLogger(TrainingController.class);

    @Autowired
    private TrainingService trainingService;

    @PostMapping("/createTraining")
    public ResponseEntity createTraining(@Valid @RequestBody TrainingRequest request) {
        log.info("createTraining :: request received for code={}", request.getTrainingCode());
        return trainingService.createTraining(request);
    }

    @GetMapping("/getTrainingById/{id}")
    public ResponseEntity getTrainingById(@PathVariable("id") Long id) {
        log.info("getTrainingById :: request received for id={}", id);
        return trainingService.getTrainingById(id);
    }

    @GetMapping("/getAllTraining")
    public ResponseEntity getAllTraining(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "trainingId") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
        log.info("getAllTraining :: request received with page={}, size={}", page, size);
        return trainingService.getAllTraining(page, size, sortBy, sortDir);
    }

    @DeleteMapping("/deleteTraining/{id}")
    public ResponseEntity deleteTraining(@PathVariable("id") Long id) {
        log.info("deleteTraining :: request received for id={}", id);
        return trainingService.deleteTraining(id);
    }

    @PutMapping("/publishTraining/{id}")
    public ResponseEntity publishTraining(@PathVariable("id") Long id) {
        log.info("publishTraining :: request received for id={}", id);
        return trainingService.publishTraining(id);
    }
}