package com.vhh.PrescriptionAppBackend.mapper; 

import org.modelmapper.ModelMapper;

import com.vhh.PrescriptionAppBackend.model.entity.Unit;
import com.vhh.PrescriptionAppBackend.model.response.UnitResponse;

public class UnitMapper {
    private static final ModelMapper modelMapper = new ModelMapper();
    public static Unit responseToModel(UnitResponse unitResponse) {
        return modelMapper.map(unitResponse, Unit.class);
    }
    public static UnitResponse modelToResponse(Unit unit) {
        return modelMapper.map(unit, UnitResponse.class);
    }
}
