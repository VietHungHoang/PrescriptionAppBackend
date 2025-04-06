package com.vhh.PrescriptionAppBackend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import com.vhh.PrescriptionAppBackend.model.entity.Country;
import com.vhh.PrescriptionAppBackend.model.response.CountryResponse;

public class CountryMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    public static List<CountryResponse> toCountryResponses(List<Country> countries) {
        if (countries == null || countries.isEmpty()) {
            return List.of();
        }
        return countries.stream()
                .map(country -> modelMapper.map(country, CountryResponse.class))
                .collect(Collectors.toList());
    }
}
