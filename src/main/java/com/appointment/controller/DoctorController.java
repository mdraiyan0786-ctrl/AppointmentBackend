package com.appointment.controller;

import com.appointment.entity.Doctor;
import com.appointment.security.JwtService;
import com.appointment.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "http://localhost:5173")
public class DoctorController {

    private final DoctorService doctorService;
    private final JwtService jwtService;

    public DoctorController(
            DoctorService doctorService,
            JwtService jwtService) {

        this.doctorService = doctorService;
        this.jwtService = jwtService;
    }

    // =========================
    // DOCTOR REGISTRATION
    // =========================

    @PostMapping("/register")
    public ResponseEntity<?> registerDoctor(
            @RequestBody Doctor doctor) {

        try {

            Doctor savedDoctor =
                    doctorService.registerDoctor(doctor);

            // Never return password
            savedDoctor.setPassword(null);

            return ResponseEntity.ok(savedDoctor);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message",
                            e.getMessage()
                    ));
        }
    }


    // =========================
    // DOCTOR LOGIN
    // =========================

    @PostMapping("/login")
    public ResponseEntity<?> loginDoctor(
            @RequestBody Map<String, String> loginRequest) {

        try {

            String email = loginRequest.get("email");
            String password = loginRequest.get("password");

            if (email == null || password == null) {

                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "message",
                                "Email and password are required"
                        ));
            }

            Doctor doctor =
                    doctorService.findByEmail(email);

            boolean passwordMatches =
                    doctorService.checkPassword(
                            password,
                            doctor.getPassword()
                    );

            if (!passwordMatches) {

                return ResponseEntity
                        .status(401)
                        .body(Map.of(
                                "message",
                                "Invalid email or password"
                        ));
            }

            // Generate JWT for doctor
            String token =
                    jwtService.generateToken(
                            doctor.getEmail(),
                            "DOCTOR"
                    );

            // Don't send password
            doctor.setPassword(null);

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "message",
                    "Doctor login successful"
            );

            response.put(
                    "token",
                    token
            );

            response.put(
                    "doctor",
                    doctor
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(401)
                    .body(Map.of(
                            "message",
                            "Invalid email or password"
                    ));
        }
    }


    // =========================
    // GET ALL DOCTORS
    // =========================

    @GetMapping
    public List<Doctor> getAllDoctors() {

        return doctorService.getAllDoctors();
    }


    // =========================
    // GET DOCTOR BY ID
    // =========================

    @GetMapping("/{id}")
    public Doctor getDoctorById(
            @PathVariable Long id) {

        return doctorService.getDoctorById(id);
    }


    // =========================
    // DELETE DOCTOR
    // =========================

    @DeleteMapping("/{id}")
    public String deleteDoctor(
            @PathVariable Long id) {

        doctorService.deleteDoctor(id);

        return "Doctor deleted successfully";
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetDoctorPassword(
            @RequestBody Map<String, String> request) {

        try {

            String email = request.get("email");
            String newPassword = request.get("newPassword");

            if (email == null || newPassword == null) {

                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "message",
                                "Email and new password are required"
                        ));
            }

            doctorService.resetPassword(
                    email,
                    newPassword
            );

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Doctor password reset successfully"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message",
                            e.getMessage()
                    ));
        }
    }
}