package com.vhh.PrescriptionAppBackend.model.request;

import lombok.Data;

import java.util.Date;

@Data
public class ScheduleAddRequest {
    private String date;
    private Double dosage;
}
