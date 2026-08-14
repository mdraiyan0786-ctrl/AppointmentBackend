package com.appointment.controller;

import com.appointment.entity.Prescription;
import com.appointment.service.PrescriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prescriptions")
@CrossOrigin(origins = "http://localhost:5173")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(
            PrescriptionService prescriptionService) {

        this.prescriptionService = prescriptionService;
    }

    // Doctor creates a prescription for an appointment
    @PostMapping("/appointment/{appointmentId}")
    public ResponseEntity<Prescription> createPrescription(
            @PathVariable Long appointmentId,
            @RequestBody PrescriptionRequest request) {

        Prescription prescription =
                prescriptionService.createPrescription(
                        appointmentId,
                        request.getPrescriptionText()
                );

        return ResponseEntity.ok(prescription);
    }

    // Patient gets prescription for an appointment
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<Prescription> getPrescription(
            @PathVariable Long appointmentId) {

        Prescription prescription =
                prescriptionService
                        .getPrescriptionByAppointmentId(
                                appointmentId
                        );

        if (prescription == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(prescription);
    }

    // Request body for creating prescription
    public static class PrescriptionRequest {

        private String prescriptionText;

        public String getPrescriptionText() {
            return prescriptionText;
        }

        public void setPrescriptionText(String prescriptionText) {
            this.prescriptionText = prescriptionText;
        }
    }
}