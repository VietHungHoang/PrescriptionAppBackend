package com.vhh.PrescriptionAppBackend.model.request;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrugInPresRequest {
    @JsonProperty("drug_id")
    private Long drugId;

    @JsonProperty("unit_id")
    private Long unitId;

    @JsonProperty("start_date")
    private String startDate;
    private String note;
    private List<ScheduleRequest> schedules;
}
