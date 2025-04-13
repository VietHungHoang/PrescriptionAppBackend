package com.vhh.PrescriptionAppBackend.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vhh.PrescriptionAppBackend.model.entity.ScheduleDoctor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleDoctorResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonFormat(pattern = "HH:mm")
    @JsonProperty("start_time")
    private LocalTime startTime;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("end_date")
    private LocalDate endDate;

    @JsonFormat(pattern = "HH:mm")
    @JsonProperty("end_time")
    private LocalTime endTime;

    public static ScheduleDoctorResponse fromScheduleDoctor(ScheduleDoctor scheduleDoctor) {
        return ScheduleDoctorResponse.builder()
                .id(scheduleDoctor.getId())
                .title(scheduleDoctor.getTitle())
                .description(scheduleDoctor.getDescription())
                .startDate(scheduleDoctor.getStartDate())
                .startTime(scheduleDoctor.getStartTime())
                .endDate(scheduleDoctor.getEndDate())
                .endTime(scheduleDoctor.getEndTime())
                .build();
    }
}
