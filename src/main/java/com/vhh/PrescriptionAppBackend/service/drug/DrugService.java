package com.vhh.PrescriptionAppBackend.service.drug;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.vhh.PrescriptionAppBackend.model.entity.Drug;
import com.vhh.PrescriptionAppBackend.model.response.DrugNameResponse;
import com.vhh.PrescriptionAppBackend.model.response.UnitResponse;
import com.vhh.PrescriptionAppBackend.repository.DrugRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DrugService implements IDrugService {
    private final DrugRepository drugRepository;

    @Override
    public List<DrugNameResponse> findAllSimple() {
        return this.drugRepository.findAllSimple();
    }
    @Override
    public void importDrugs(List<Drug> drugs) {
        this.drugRepository.saveAll(drugs);
    }
}
