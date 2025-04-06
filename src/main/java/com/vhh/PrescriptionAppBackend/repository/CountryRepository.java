package com.vhh.PrescriptionAppBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vhh.PrescriptionAppBackend.model.entity.Country;

public interface CountryRepository extends JpaRepository<Country, Long>{
    
}
