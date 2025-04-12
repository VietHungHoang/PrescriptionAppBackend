package com.vhh.PrescriptionAppBackend.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleDoctorRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @Size(max = 255, message = "Description can't exceed 255 characters")
    private String description;

    @NotNull(message = "Start date is required")
    @JsonProperty("start_date")
    private LocalDate startDate;

    @NotNull(message = "Start time is required")
    @JsonProperty("start_time")
    private LocalTime startTime;

    @NotNull(message = "End date is required")
    @JsonProperty("end_date")
    private LocalDate endDate;

    @NotNull(message = "End time is required")
    @JsonProperty("end_time")
    private LocalTime endTime;

}
