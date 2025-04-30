package com.michaelbecker.tankmaintainer.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Data
public class WaterChange {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tank_id")
    @JsonBackReference
    private Tank tank;

    private LocalDateTime date;

    // Use gallons since you're using imperial units
    private BigDecimal volumeGallons;

    private String notes;
}