package com.appointment.repository;

import com.appointment.entity.Doctor;
import com.appointment.entity.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorAvailabilityRepository
        extends JpaRepository<DoctorAvailability, Long> {

    List<DoctorAvailability> findByDoctorIdAndAvailableDate(
            Long doctorId,
            LocalDate availableDate
    );

    Optional<DoctorAvailability>
    findByDoctorAndAvailableDateAndAvailableTime(
            Doctor doctor,
            LocalDate availableDate,
            LocalTime availableTime
    );
}