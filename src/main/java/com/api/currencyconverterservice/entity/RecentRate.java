package com.api.currencyconverterservice.entity;
import java.math.BigDecimal;
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

public class RecentRate{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; 

    private LocalDateTime requestTime;
    private String fromCurrency;
    private String toCurrency;
    private BigDecimal baseAmount;
    private BigDecimal convertedAmount;


}