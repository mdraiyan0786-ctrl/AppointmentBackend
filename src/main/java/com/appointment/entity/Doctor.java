package com.appointment.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "doctors")
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

    public Doctor() {
    }

    public Doctor(Long id, String name, String specialization, String qualification, Integer experience, Double consultationFee, String medicalStore, String availableTime, Double rating, String imageUrl) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.qualification = qualification;
        this.experience = experience;
        this.consultationFee = consultationFee;
        this.medicalStore = medicalStore;
        this.availableTime = availableTime;
        this.rating = rating;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(Double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public String getMedicalStore() {
        return medicalStore;
    }

    public void setMedicalStore(String medicalStore) {
        this.medicalStore = medicalStore;
    }

    public String getAvailableTime() {
        return availableTime;
    }

    public void setAvailableTime(String availableTime) {
        this.availableTime = availableTime;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
