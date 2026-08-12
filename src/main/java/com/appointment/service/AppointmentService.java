package com.appointment.service;

import com.appointment.entity.Appointment;
import com.appointment.entity.Doctor;
import com.appointment.entity.DoctorAvailability;
import com.appointment.entity.User;
import com.appointment.repository.AppointmentRepository;
import com.appointment.repository.DoctorAvailabilityRepository;
import com.appointment.repository.DoctorRepository;
import com.appointment.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository availabilityRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            UserRepository userRepository,
            DoctorRepository doctorRepository,
            DoctorAvailabilityRepository availabilityRepository) {

        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.availabilityRepository = availabilityRepository;
    }

    public Appointment bookAppointment(
            Appointment appointment,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Doctor doctor = doctorRepository.findById(
                appointment.getDoctor().getId()
        ).orElseThrow(() ->
                new RuntimeException("Doctor not found"));

        DoctorAvailability availability =
                availabilityRepository
                        .findByDoctorAndAvailableDateAndAvailableTime(
                                doctor,
                                appointment.getAppointmentDate(),
                                appointment.getAppointmentTime()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Doctor is not available at this time"
                                ));

        long bookedPatients =
                appointmentRepository
                        .countByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatus(
                                doctor.getId(),
                                appointment.getAppointmentDate(),
                                appointment.getAppointmentTime(),
                                "BOOKED"
                        );

        if (bookedPatients >= availability.getMaxPatients()) {

            throw new RuntimeException(
                    "This time slot is full. Please choose another time."
            );
        }

        appointment.setUser(user);
        appointment.setDoctor(doctor);
        appointment.setStatus("BOOKED");

        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository
                .findById(id)
                .orElse(null);
    }

    public void cancelAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    public List<Appointment> getMyAppointment(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not Found"));

        return appointmentRepository.findByUserId(
                user.getId()
        );
    }
}