package com.vhh.PrescriptionAppBackend.service.unit;

import java.util.List;

import org.springframework.stereotype.Service;
import com.vhh.PrescriptionAppBackend.mapper.UnitMapper;
import com.vhh.PrescriptionAppBackend.model.response.UnitResponse;
import com.vhh.PrescriptionAppBackend.repository.UnitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnitService implements IUnitService {
    private final UnitRepository unitRepository;

    @Override
    public List<UnitResponse> findAll() {
        return this.unitRepository.findAll().stream().map(UnitMapper::modelToResponse).toList();
    }
}
