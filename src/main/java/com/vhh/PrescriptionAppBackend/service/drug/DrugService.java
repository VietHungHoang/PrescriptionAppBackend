package com.vhh.PrescriptionAppBackend.service.drug;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vhh.PrescriptionAppBackend.model.entity.Drug;
import com.vhh.PrescriptionAppBackend.model.response.DrugNameResponseKiet;
import com.vhh.PrescriptionAppBackend.repository.kiet.DrugRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DrugService implements IDrugService {
    private final DrugRepository drugRepository;

    @Override
    public List<DrugNameResponseKiet> findAllSimple() {
        return this.drugRepository.findAllSimple();
    }
    @Override
    public void importDrugs(List<Drug> drugs) {
        this.drugRepository.saveAll(drugs);
    }
}
