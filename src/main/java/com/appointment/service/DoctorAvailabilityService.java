package com.appointment.service;

import com.appointment.entity.Doctor;
import com.appointment.entity.DoctorAvailability;
import com.appointment.repository.AppointmentRepository;
import com.appointment.repository.DoctorAvailabilityRepository;
import com.appointment.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DoctorAvailabilityService {

    private final DoctorAvailabilityRepository availabilityRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public DoctorAvailabilityService(
            DoctorAvailabilityRepository availabilityRepository,
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository) {

        this.availabilityRepository = availabilityRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public DoctorAvailability createAvailability(
            Long doctorId,
            DoctorAvailability availability) {

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() ->
                        new RuntimeException("Doctor not found"));

        if (availability.getMaxPatients() == null ||
                availability.getMaxPatients() <= 0) {

            throw new RuntimeException(
                    "Maximum patients must be greater than 0");
        }

        availability.setDoctor(doctor);

        return availabilityRepository.save(availability);
    }

    public List<DoctorAvailability> getAvailabilityByDoctorAndDate(
            Long doctorId,
            LocalDate date) {

        List<DoctorAvailability> slots =
                availabilityRepository
                        .findByDoctorIdAndAvailableDate(
                                doctorId,
                                date
                        );

        for (DoctorAvailability slot : slots) {

            long bookedPatients =
                    appointmentRepository.countBookedAppointments(
                            doctorId,
                            date,
                            slot.getAvailableTime()
                    );

            int remaining =
                    slot.getMaxPatients()
                            - (int) bookedPatients;

            if (remaining < 0) {
                remaining = 0;
            }

            slot.setRemainingPatients(remaining);
        }

        return slots;
    }
}