package com.doritech.tmsservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.SubProductRequest;
import com.doritech.tmsservice.service.SubProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tmsService/api/sub-products")
public class SubProductController {

    private static final Logger log = LoggerFactory.getLogger(SubProductController.class);

    private final SubProductService subProductService;

    public SubProductController(SubProductService subProductService) {
        this.subProductService = subProductService;
    }

    @PostMapping("/createSubProduct")
    public ResponseEntity createSubProduct(@Valid @RequestBody List<SubProductRequest> subProductRequest) {
        log.info("createSubProduct :: request received with size={}", subProductRequest.size());
        return subProductService.createSubProduct(subProductRequest);
    }

    @GetMapping("/getSubProductById/{id}")
    public ResponseEntity getSubProductById(@PathVariable("id") Long id) {
        log.info("getSubProductById :: request received for id={}", id);
        return subProductService.getSubProductById(id);
    }

    @GetMapping("/getAllSubProduct")
    public ResponseEntity getAllSubProduct(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "subProductId") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
        log.info("getAllSubProduct :: request received with page={}, size={}", page, size);
        return subProductService.getAllSubProduct(page, size, sortBy, sortDir);
    }

    @GetMapping("/getSubProductsByProductId/{productId}")
    public ResponseEntity getSubProductsByProductId(@PathVariable("productId") Long productId) {
        log.info("getSubProductsByProductId :: request received for productId={}", productId);
        return subProductService.getSubProductsByProductId(productId);
    }
}