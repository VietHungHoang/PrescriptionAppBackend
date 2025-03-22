package com.vhh.PrescriptionAppBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vhh.PrescriptionAppBackend.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}