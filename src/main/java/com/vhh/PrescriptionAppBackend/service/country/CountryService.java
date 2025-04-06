package com.vhh.PrescriptionAppBackend.service.country;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vhh.PrescriptionAppBackend.model.entity.Country;
import com.vhh.PrescriptionAppBackend.repository.CountryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CountryService implements ICountryService {

    private final CountryRepository countryRepository;

    @Override
    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

}
