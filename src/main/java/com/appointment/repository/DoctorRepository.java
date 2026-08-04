package com.appointment.repository;

import com.appointment.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.print.Doc;

public interface DoctorRepository extends JpaRepository<Doctor,Long> {
}
