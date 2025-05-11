package com.michaelbecker.tankmaintainer.dto;


import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class TankParamRequest {
    public UUID tankId;
    public String paramType;
    public BigDecimal value;
    public String unit;
    public String notes;
    public OffsetDateTime timestamp;
}