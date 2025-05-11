package com.michaelbecker.tankmaintainer.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class WaterChangeRequest {
    private UUID tankId;
    private OffsetDateTime date;
    private BigDecimal volumeGallons;
    private String notes;
}