package com.appointment.service;

import com.appointment.entity.Appointment;
import com.appointment.entity.Prescription;
import com.appointment.repository.AppointmentRepository;
import com.appointment.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;

    public PrescriptionService(
            PrescriptionRepository prescriptionRepository,
            AppointmentRepository appointmentRepository) {

        this.prescriptionRepository = prescriptionRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public Prescription createPrescription(
            Long appointmentId,
            String prescriptionText) {

        Appointment appointment =
                appointmentRepository.findById(appointmentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Appointment not found"
                                ));

        if (prescriptionText == null ||
                prescriptionText.trim().isEmpty()) {

            throw new RuntimeException(
                    "Prescription cannot be empty"
            );
        }

        if (prescriptionRepository
                .existsByAppointmentId(appointmentId)) {

            throw new RuntimeException(
                    "Prescription already exists for this appointment"
            );
        }

        Prescription prescription = new Prescription();

        prescription.setAppointment(appointment);
        prescription.setPrescriptionText(prescriptionText.trim());

        return prescriptionRepository.save(prescription);
    }

    public Prescription getPrescriptionByAppointmentId(
            Long appointmentId) {

        return prescriptionRepository
                .findByAppointmentId(appointmentId)
                .orElse(null);
    }
}