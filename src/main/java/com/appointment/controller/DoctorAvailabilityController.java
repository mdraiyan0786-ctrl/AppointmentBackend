package com.appointment.controller;

import com.appointment.entity.DoctorAvailability;
import com.appointment.service.DoctorAvailabilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/availability")
@CrossOrigin(origins = "http://localhost:5173")
public class DoctorAvailabilityController {
    private final DoctorAvailabilityService availabilityService;

    public DoctorAvailabilityController(
            DoctorAvailabilityService availabilityService) {

        this.availabilityService = availabilityService;
    }

    @PostMapping("/doctor/{doctorId}")
    public ResponseEntity<DoctorAvailability> createAvailability(
            @PathVariable Long doctorId,
            @RequestBody DoctorAvailability availability) {

        return ResponseEntity.ok(
                availabilityService.createAvailability(
                        doctorId,
                        availability
                )
        );
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<DoctorAvailability>> getAvailability(
            @PathVariable Long doctorId,
            @RequestParam LocalDate date) {

        return ResponseEntity.ok(
                availabilityService.getDoctorAvailability(
                        doctorId,
                        date
                )
        );
    }
}
