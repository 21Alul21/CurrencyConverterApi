package com.api.currencyconverterservice.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.currencyconverterservice.entity.RecentRateEntity;

public interface RecentRateRepository extends JpaRepository<RecentRateEntity, UUID> {

}
