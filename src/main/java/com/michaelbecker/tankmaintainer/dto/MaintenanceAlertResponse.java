package com.michaelbecker.tankmaintainer.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record MaintenanceAlertResponse(
    UUID tankId,
    String tankName,
    LocalDate lastChangeDate,
    LocalDate nextDate,
    long daysRemaining,
    BigDecimal volumeGallons,
    String species
) {}
