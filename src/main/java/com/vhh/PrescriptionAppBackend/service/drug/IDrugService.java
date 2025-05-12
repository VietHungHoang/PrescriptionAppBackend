package com.vhh.PrescriptionAppBackend.service.drug;

import java.util.List;
import com.vhh.PrescriptionAppBackend.model.entity.Drug;
import com.vhh.PrescriptionAppBackend.model.response.DrugNameResponse;

public interface IDrugService {
    List<DrugNameResponse> findAllSimple();
    void importDrugs(List<Drug> drugs);
    List<Drug> findAll();
    List<Drug> searchDrugs(String query);
    Drug findById(Long id);
}