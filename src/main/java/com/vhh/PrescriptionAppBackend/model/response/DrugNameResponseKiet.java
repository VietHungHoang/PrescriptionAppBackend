package com.vhh.PrescriptionAppBackend.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrugNameResponseKiet {
    private Long id;
    private String name;
}
