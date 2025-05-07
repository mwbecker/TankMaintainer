package com.michaelbecker.tankmaintainer.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
public class Feeding {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tank_id")
    @JsonIgnore
    private Tank tank;

    private LocalDateTime timestamp;
    private String foodType; // e.g., BacterAE, snowflake, spinach
    private String amount;   // e.g., "1/16 tsp" or "half a pellet"
    private String notes;
}