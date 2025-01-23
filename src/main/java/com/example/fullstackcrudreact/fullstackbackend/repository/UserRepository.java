package com.example.fullstackcrudreact.fullstackbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fullstackcrudreact.fullstackbackend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
