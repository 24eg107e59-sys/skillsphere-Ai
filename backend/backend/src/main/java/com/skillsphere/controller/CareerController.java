package com.skillsphere.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillsphere.dto.CareerOption;
import com.skillsphere.service.CareerService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/careers")
public class CareerController {

    private final CareerService careerService;

    public CareerController(CareerService careerService) {
        this.careerService = careerService;
    }

    @GetMapping
    public ResponseEntity<List<CareerOption>> getCareers() {
        return ResponseEntity.ok(careerService.getAllCareers());
    }
}
