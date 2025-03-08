package com.example.fullstackcrudreact.fullstackbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fullstackcrudreact.fullstackbackend.model.GeneratedPaymentSlip;

public interface GeneratedPaymentSlipRepository extends JpaRepository<GeneratedPaymentSlip, Long> {

}
