package com.vhh.PrescriptionAppBackend.service.country;

import java.util.List;
import java.util.Optional;

import com.vhh.PrescriptionAppBackend.model.entity.Country;

public interface ICountryService {
    List<Country> getAllCountries();
}
