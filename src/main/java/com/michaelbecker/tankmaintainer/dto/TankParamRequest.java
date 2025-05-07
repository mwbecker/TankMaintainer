package com.michaelbecker.tankmaintainer.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TankParamRequest {
    public UUID tankId;
    public String paramType;
    public BigDecimal value;
    public String unit;
    public String notes;
    public LocalDateTime timestamp;
}