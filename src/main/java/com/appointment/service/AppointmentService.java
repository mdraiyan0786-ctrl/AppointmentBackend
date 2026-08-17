package com.appointment.service;

import com.appointment.entity.Appointment;
import com.appointment.entity.Doctor;
import com.appointment.entity.DoctorAvailability;
import com.appointment.entity.User;
import com.appointment.repository.AppointmentRepository;
import com.appointment.repository.DoctorAvailabilityRepository;
import com.appointment.repository.DoctorRepository;
import com.appointment.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository availabilityRepository;
    private final NotificationService notificationService;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            UserRepository userRepository,
            DoctorRepository doctorRepository,
            DoctorAvailabilityRepository availabilityRepository,
            NotificationService notificationService) {

        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.availabilityRepository = availabilityRepository;
        this.notificationService = notificationService;
    }

    // ==========================================
    // BOOK APPOINTMENT
    // ==========================================

    public Appointment bookAppointment(
            Appointment appointment,
            String email) {

        // 1. Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // 2. Find doctor
        Doctor doctor = doctorRepository.findById(
                appointment.getDoctor().getId()
        ).orElseThrow(() ->
                new RuntimeException("Doctor not found"));

        // 3. Check duplicate booking
        boolean alreadyBooked =
                appointmentRepository
                        .existsByUserIdAndDoctorIdAndAppointmentDateAndStatus(
                                user.getId(),
                                doctor.getId(),
                                appointment.getAppointmentDate(),
                                "BOOKED"
                        );

        if (alreadyBooked) {

            throw new RuntimeException(
                    "You have already booked an appointment with this doctor on this day."
            );
        }

        // 4. Check doctor availability
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

        // 5. Count booked patients
        long bookedPatients =
                appointmentRepository
                        .countByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatus(
                                doctor.getId(),
                                appointment.getAppointmentDate(),
                                appointment.getAppointmentTime(),
                                "BOOKED"
                        );

        // 6. Check maximum patients
        if (bookedPatients >= availability.getMaxPatients()) {

            throw new RuntimeException(
                    "This time slot is full. Please choose another time."
            );
        }

        // 7. Set appointment details
        appointment.setUser(user);
        appointment.setDoctor(doctor);
        appointment.setStatus("BOOKED");

        // 8. Save appointment
        Appointment savedAppointment =
                appointmentRepository.save(appointment);

        // 9. Create BOOKED notification
        notificationService.createNotification(
                user,
                "Your appointment with "
                        + doctor.getName()
                        + " has been booked successfully for "
                        + appointment.getAppointmentDate()
                        + " at "
                        + appointment.getAppointmentTime()
        );

        return savedAppointment;
    }

    // ==========================================
    // GET ALL APPOINTMENTS
    // ==========================================

    public List<Appointment> getAllAppointments() {

        return appointmentRepository.findAll();
    }

    // ==========================================
    // GET APPOINTMENT BY ID
    // ==========================================

    public Appointment getAppointmentById(Long id) {

        return appointmentRepository
                .findById(id)
                .orElse(null);
    }

    // ==========================================
    // CANCEL APPOINTMENT
    // ==========================================

    public void cancelAppointment(Long id) {

        Appointment appointment =
                appointmentRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Appointment Not Found"
                                ));

        if ("COMPLETED".equals(appointment.getStatus())) {

            throw new RuntimeException(
                    "Completed Appointments Cannot be Cancelled"
            );
        }

        if ("CANCELLED".equals(appointment.getStatus())) {

            throw new RuntimeException(
                    "Appointment is already cancelled."
            );
        }

        // Change status
        appointment.setStatus("CANCELLED");

        // Save appointment
        appointmentRepository.save(appointment);

        // Create CANCELLED notification
        notificationService.createNotification(
                appointment.getUser(),
                "Your appointment with "
                        + appointment.getDoctor().getName()
                        + " on "
                        + appointment.getAppointmentDate()
                        + " at "
                        + appointment.getAppointmentTime()
                        + " has been cancelled."
        );
    }

    // ==========================================
    // GET LOGGED-IN USER APPOINTMENTS
    // ==========================================

    public List<Appointment> getMyAppointment(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not Found"));

        return appointmentRepository.findByUserId(
                user.getId()
        );
    }

    // ==========================================
    // AUTOMATICALLY COMPLETE APPOINTMENTS
    // ==========================================

    public void updateCompletedAppointments() {

        List<Appointment> appointments =
                appointmentRepository.findAll();

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        for (Appointment appointment : appointments) {

            // Only BOOKED appointments can become COMPLETED
            if (!"BOOKED".equals(appointment.getStatus())) {
                continue;
            }

            LocalDate appointmentDate =
                    appointment.getAppointmentDate();

            LocalTime appointmentTime =
                    appointment.getAppointmentTime();

            boolean appointmentPassed =
                    appointmentDate.isBefore(today)
                            ||
                            (
                                    appointmentDate.isEqual(today)
                                            &&
                                            appointmentTime.isBefore(now)
                            );

            if (appointmentPassed) {

                // Change status
                appointment.setStatus("COMPLETED");

                // Save appointment
                appointmentRepository.save(appointment);

                // Create COMPLETED notification
                notificationService.createNotification(
                        appointment.getUser(),
                        "Your appointment with "
                                + appointment.getDoctor().getName()
                                + " on "
                                + appointment.getAppointmentDate()
                                + " at "
                                + appointment.getAppointmentTime()
                                + " has been completed."
                );
            }
        }
    }

    public List<Appointment> getDoctorAppointments(String email) {

        Doctor doctor = doctorRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Doctor not found"));

        return appointmentRepository.findByDoctorId(
                doctor.getId()
        );
    }

    // ==========================================
    // RUN EVERY 60 SECONDS
    // ==========================================

    @Scheduled(fixedRate = 60000)
    public void automaticallyCompleteAppointments() {

        updateCompletedAppointments();
    }

    public Appointment completeAppointment(Long id) {

        Appointment appointment =
                appointmentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Appointment not found"
                                )
                        );

        if ("CANCELLED".equals(appointment.getStatus())) {
            throw new RuntimeException(
                    "Cancelled appointment cannot be completed"
            );
        }

        if ("COMPLETED".equals(appointment.getStatus())) {
            throw new RuntimeException(
                    "Appointment is already completed"
            );
        }

        appointment.setStatus("COMPLETED");

        Appointment savedAppointment =
                appointmentRepository.save(appointment);

        notificationService.createNotification(
                appointment.getUser(),
                "Your appointment with "
                        + appointment.getDoctor().getName()
                        + " on "
                        + appointment.getAppointmentDate()
                        + " at "
                        + appointment.getAppointmentTime()
                        + " has been completed."
        );

        return savedAppointment;
    }
}