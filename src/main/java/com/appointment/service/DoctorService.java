package com.appointment.service;

import com.appointment.entity.Doctor;
import com.appointment.repository.DoctorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.util.List;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    public DoctorService(DoctorRepository doctorRepository,PasswordEncoder passwordEncoder) {
        this.doctorRepository = doctorRepository;
        this.passwordEncoder=passwordEncoder;
    }

    public Doctor saveDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id).orElse(null);
    }

    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }

    // =========================
    // DOCTOR REGISTRATION
    // =========================
    public Doctor registerDoctor(Doctor doctor){
        if(doctorRepository.existsByEmail(doctor.getEmail())){
            throw new RuntimeException("Doctor already Exist");
        }
        if(doctor.getPhone()!=null&&doctorRepository.existsByPhone(doctor.getPhone())){
            throw new RuntimeException("Doctor Phone Number ALready Exist");
        }
        doctor.setPassword(
                passwordEncoder.encode(doctor.getPassword())
        );

        return doctorRepository.save(doctor);
    }
    // =========================
    // FIND DOCTOR BY EMAIL
    // =========================

    public Doctor findByEmail(String email) {

        return doctorRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Doctor not found")
                );
    }

    // =========================
    // CHECK DOCTOR PASSWORD
    // =========================

    public boolean checkPassword(
            String rawPassword,
            String encodedPassword) {

        return passwordEncoder.matches(
                rawPassword,
                encodedPassword
        );
    }
}
