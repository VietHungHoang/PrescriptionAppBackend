package com.vhh.PrescriptionAppBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.vhh.PrescriptionAppBackend.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}