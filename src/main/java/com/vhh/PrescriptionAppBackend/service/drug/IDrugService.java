package com.vhh.PrescriptionAppBackend.service.drug;

import java.util.List;

import com.vhh.PrescriptionAppBackend.model.entity.Drug;
import com.vhh.PrescriptionAppBackend.model.response.DrugNameResponseKiet;

public interface IDrugService {
    List<DrugNameResponseKiet> findAllSimple();
    void importDrugs(List<Drug> drugs);
} 
