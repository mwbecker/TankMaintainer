package com.michaelbecker.tankmaintainer.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Data
public class TankParam {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tank_id")
    @JsonBackReference
    private Tank tank;

    private LocalDateTime timestamp;

    // The type of parameter being tracked (pH, KH, ammonia, etc.)
    @Enumerated(EnumType.STRING)
    private ParamType paramType;

    private BigDecimal value;

    // Units for the parameter (e.g., °dKH, ppm, etc.)
    private String unit;

    private String notes;
}