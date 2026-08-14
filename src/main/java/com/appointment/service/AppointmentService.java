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
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.LocalTime;
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

        // 1. Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // 2. Find doctor
        Doctor doctor = doctorRepository.findById(
                appointment.getDoctor().getId()
        ).orElseThrow(() ->
                new RuntimeException("Doctor not found"));

        // 3. Check if this patient already booked
        // this same doctor on the same date
        boolean alreadyBooked =
                appointmentRepository
                        .existsByUserIdAndDoctorIdAndAppointmentDateAndStatus(
                                user.getId(),
                                doctor.getId(),
                                appointment.getAppointmentDate(),
                                "BOOKED"
                        );

        if (alreadyBooked) {
            System.out.println(
                    "BOOKING REJECTED: User " + user.getEmail()
                            + " already has an appointment with Doctor "
                            + doctor.getName()
                            + " on " + appointment.getAppointmentDate()
            );

            throw new RuntimeException(
                    "You have already booked an appointment with this doctor on this day."
            );
        }

        // 4. Check doctor's availability for selected date/time
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

        // 5. Count already booked patients
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
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(()->
                new RuntimeException("Appointment Not Found"));

        if("COMPLETED".equals(appointment.getStatus())){
            throw new RuntimeException("Completed Appointments Cannot be Cancelled");
        }
        if ("CANCELLED".equals(appointment.getStatus())) {
            throw new RuntimeException( "Appointment is already cancelled." );
        }

        appointment.setStatus("CANCELLED");
        appointmentRepository.save(appointment);
    }

    public List<Appointment> getMyAppointment(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not Found"));

        return appointmentRepository.findByUserId(
                user.getId()
        );
    }

    public void updateCompletedAppointments(){
        List<Appointment> appointments = appointmentRepository.findAll();
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        for(Appointment appointment:appointments){
            if (!"BOOKED".equals(appointment.getStatus())) { continue; }
            LocalDate appointmentDate = appointment.getAppointmentDate();
            LocalTime appointmentTime = appointment.getAppointmentTime();
            boolean appointmentPassed = appointmentDate.isBefore(today) || ( appointmentDate.isEqual(today) && appointmentTime.isBefore(now) );
            if (appointmentPassed) { appointment.setStatus("COMPLETED");
                appointmentRepository.save(appointment); }
        }
    }

    @Scheduled(fixedRate = 60000)
    public void automaticallyCompleteAppointments() {
        updateCompletedAppointments();
    }


}