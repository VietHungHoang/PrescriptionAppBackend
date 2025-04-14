package com.vhh.PrescriptionAppBackend.controller;

import com.vhh.PrescriptionAppBackend.model.request.ScheduleDoctorRequest;
import com.vhh.PrescriptionAppBackend.model.response.ScheduleDoctorResponse;
import com.vhh.PrescriptionAppBackend.service.schedule_doctor.ScheduleDoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/schedules")
@RequiredArgsConstructor
public class ScheduleDoctorController {

    private final ScheduleDoctorService service;

    // Lấy tất cả lịch hẹn
    @GetMapping("/getAll")
    public ResponseEntity<List<ScheduleDoctorResponse>> getAll() {
        List<ScheduleDoctorResponse> scheduleResponses = service.getAllSchedules();
        return ResponseEntity.ok(scheduleResponses);
    }

    // Lấy lịch hẹn theo id
    @GetMapping("/getById/{id}")
    public ResponseEntity<ScheduleDoctorResponse> getById(@PathVariable Long id) {
        return service.getScheduleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Tạo lịch hẹn mới
    @PostMapping("/create")
    public ResponseEntity<ScheduleDoctorResponse> create(@RequestBody ScheduleDoctorRequest scheduleRequest) {
        ScheduleDoctorResponse response = service.createSchedule(scheduleRequest);
        return ResponseEntity.ok(response);
    }

    // Cập nhật lịch hẹn
    @PutMapping("/updateById/{id}")
    public ResponseEntity<ScheduleDoctorResponse> update(@PathVariable Long id, @RequestBody ScheduleDoctorRequest updatedRequest) {
        ScheduleDoctorResponse response = service.updateSchedule(id, updatedRequest);
        return ResponseEntity.ok(response);
    }

    // Xóa lịch hẹn
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    // Lấy lịch hẹn trong khoảng thời gian
    @GetMapping("/get-by-date-range")
    public ResponseEntity<List<ScheduleDoctorResponse>> getByDateRange(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        List<ScheduleDoctorResponse> scheduleResponses = service.getSchedulesByDateRange(start, end);
        return ResponseEntity.ok(scheduleResponses);
    }
}