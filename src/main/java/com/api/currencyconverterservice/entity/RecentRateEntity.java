package com.api.currencyconverterservice.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class RecentRateEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; 

    private String baseSymbol;
    private LocalDateTime requestTime;
    private boolean status;
}