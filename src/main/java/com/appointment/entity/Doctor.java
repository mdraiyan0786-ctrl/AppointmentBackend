package com.appointment.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String specialization;

    private String qualification;

    private Integer experience;

    private Double consultationFee;

    private String medicalStore;

    private String availableTime;

    private Double rating;

    private String imageUrl;
}
