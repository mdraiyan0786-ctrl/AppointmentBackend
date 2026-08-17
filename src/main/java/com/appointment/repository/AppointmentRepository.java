package com.appointment.repository;

import com.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment,Long> {
    
    List<Appointment> findByUserId(Long userId);

    long countByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatus(
            Long doctorId,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            String status
    );

    @Query("""
    SELECT COUNT(a)
    FROM Appointment a
    WHERE a.doctor.id = :doctorId
    AND a.appointmentDate = :date
    AND a.appointmentTime = :time
    AND a.status = 'BOOKED'
    """)
    long countBookedAppointments(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("time") LocalTime time
    );

    boolean existsByUserIdAndDoctorIdAndAppointmentDateAndStatus(
            Long userId,
            Long doctorId,
            LocalDate appointmentDate,
            String status
    );

    List<Appointment> findByDoctorId(Long doctorId);
}
