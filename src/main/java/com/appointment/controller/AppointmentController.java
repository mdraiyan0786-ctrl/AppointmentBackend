package com.appointment.controller;
import com.appointment.entity.Appointment;
import com.appointment.service.AppointmentService;
//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "http://localhost:5173")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public Appointment bookAppointment(
            @RequestBody Appointment appointment,
            Authentication authentication) {

        String email = authentication.getName();

        return appointmentService.bookAppointment(
                appointment,
                email
        );
    }

    @GetMapping
    public List<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    // Get logged-in user's appointments
    @GetMapping("/my")
    public List<Appointment> getMyAppointments(
            Authentication authentication) {

        String email = authentication.getName();

        return appointmentService.getMyAppointment(email);
    }

    @GetMapping("/{id}")
    public Appointment getAppointmentById(
            @PathVariable Long id) {

        return appointmentService.getAppointmentById(id);
    }

    @DeleteMapping("/{id}")
    public String cancelAppointment(
            @PathVariable Long id) {

        appointmentService.cancelAppointment(id);

        return "Appointment cancelled successfully";
    }
}
