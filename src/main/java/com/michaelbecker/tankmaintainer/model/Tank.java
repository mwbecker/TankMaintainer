package com.michaelbecker.tankmaintainer.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Data
public class Tank {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private String name;
    private BigDecimal volumeGallons;
    private String species;
    private String notes;

    @OneToMany(mappedBy = "tank", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<TankParam> parameters;

    @OneToMany(mappedBy = "tank", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<WaterChange> waterChanges;

    @OneToMany(mappedBy = "tank", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Feeding> feedings;
        
}