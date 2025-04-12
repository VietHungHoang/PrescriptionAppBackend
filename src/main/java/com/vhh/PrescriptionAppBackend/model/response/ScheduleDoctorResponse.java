package com.vhh.PrescriptionAppBackend.model.response;

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

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("start_time")
    private LocalTime startTime;

    @JsonProperty("end_date")
    private LocalDate endDate;

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
