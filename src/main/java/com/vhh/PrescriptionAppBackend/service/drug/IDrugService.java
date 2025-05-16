package com.vhh.PrescriptionAppBackend.service.drug;

import java.util.List;

import com.vhh.PrescriptionAppBackend.model.entity.Drug;
import com.vhh.PrescriptionAppBackend.model.response.DrugNameResponse;
import com.vhh.PrescriptionAppBackend.model.response.UnitResponse;

public interface IDrugService {
    List<DrugNameResponse> findAllSimple();
    void importDrugs(List<Drug> drugs);
} 
