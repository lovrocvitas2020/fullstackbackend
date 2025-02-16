package com.example.fullstackcrudreact.fullstackbackend.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fullstackcrudreact.fullstackbackend.model.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);
    
    void deleteByToken(String token);

    Optional<PasswordResetToken> findByEmail(String email);

}
