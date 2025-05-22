package com.vhh.PrescriptionAppBackend.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionResponseKiet {
    private Long id;
    private String name;
    private String hospital;
    private String doctorName;
    private LocalDate consultationDate;
    private LocalDate followUpDate;
    private Integer status;
    private List<DrugInPrescriptionResponseKiet> drugs;
}
