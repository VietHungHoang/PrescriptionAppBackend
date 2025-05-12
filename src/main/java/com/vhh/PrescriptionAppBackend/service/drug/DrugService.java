package com.vhh.PrescriptionAppBackend.service.drug;

import java.util.List;
import org.springframework.stereotype.Service;
import com.vhh.PrescriptionAppBackend.model.entity.Drug;
import com.vhh.PrescriptionAppBackend.model.response.DrugNameResponse;
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

    @Override
    public List<Drug> findAll() {
        return drugRepository.findAll();
    }

    @Override
    public List<Drug> searchDrugs(String query) {
        return drugRepository.findByNameContainingIgnoreCase(query);
    }

    @Override
    public Drug findById(Long id) {
        return drugRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Drug not found with id: " + id));
    }
}