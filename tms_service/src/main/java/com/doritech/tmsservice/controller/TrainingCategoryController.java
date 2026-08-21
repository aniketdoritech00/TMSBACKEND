package com.doritech.tmsservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.TrainingCategoryRequest;
import com.doritech.tmsservice.service.TrainingCategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tms/training-categories")
public class TrainingCategoryController {

    private static final Logger log = LoggerFactory.getLogger(TrainingCategoryController.class);

    @Autowired
    private TrainingCategoryService trainingCategoryService;

    @PostMapping("/createTrainingCategory")
    public ResponseEntity createTrainingCategory(@Valid @RequestBody TrainingCategoryRequest request) {
        log.info("createTrainingCategory :: request received for name={}", request.getCategoryName());
        return trainingCategoryService.createTrainingCategory(request);
    }

    @GetMapping("/getTrainingCategoryById/{id}")
    public ResponseEntity getTrainingCategoryById(@PathVariable("id") Long id) {
        log.info("getTrainingCategoryById :: request received for id={}", id);
        return trainingCategoryService.getTrainingCategoryById(id);
    }

    @GetMapping("/getAllTrainingCategory")
    public ResponseEntity getAllTrainingCategory(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "trainingCategoryId") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
        log.info("getAllTrainingCategory :: request received with page={}, size={}", page, size);
        return trainingCategoryService.getAllTrainingCategory(page, size, sortBy, sortDir);
    }

    @DeleteMapping("/deleteTrainingCategory/{id}")
    public ResponseEntity deleteTrainingCategory(@PathVariable("id") Long id) {
        log.info("deleteTrainingCategory :: request received for id={}", id);
        return trainingCategoryService.deleteTrainingCategory(id);
    }
}