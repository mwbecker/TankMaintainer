package com.michaelbecker.tankmaintainer.dto;

import java.time.LocalDate;
import java.util.UUID;

public record NextMaintenanceResponse(
    UUID tankId,
    LocalDate nextDate,
    long daysRemaining
) {}