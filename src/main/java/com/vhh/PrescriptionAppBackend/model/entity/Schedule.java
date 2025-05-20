package com.vhh.PrescriptionAppBackend.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.vhh.PrescriptionAppBackend.model.request.ScheduleRequest;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime date;
    private Boolean status;

    @ManyToOne
    @JoinColumn(name="drug_in_pres_id")
    DrugInPrescription drugInPrescription;

    @ManyToOne
    @JoinColumn(name="time_dosage_id")
    Dosage timeDosage;

    public static Schedule responseToEntity(ScheduleRequest scheduleRequest) {
        return Schedule.builder()
            // .date(scheduleRequest.getDate().toString())
            .build();
    }
}