package com.vhh.PrescriptionAppBackend.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrugInPrescriptionResponse {
    private String drugName;
    private String unitName;
    private List<DosageResponse> dosages;
    private List<ScheduleResponse> schedules;
}
