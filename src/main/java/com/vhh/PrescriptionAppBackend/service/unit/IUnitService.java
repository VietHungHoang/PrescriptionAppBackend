package com.vhh.PrescriptionAppBackend.service.unit;

import java.util.List;

import com.vhh.PrescriptionAppBackend.model.response.UnitResponse;

public interface IUnitService {
    List<UnitResponse> findAll();
} 
